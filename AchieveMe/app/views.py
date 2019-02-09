from django.shortcuts import render, redirect
from django.http import HttpResponse

from django.contrib.auth.models import User
from django.contrib.sites.shortcuts import get_current_site
from django.contrib.auth import login, authenticate
from .forms import SignupForm
from .forms import SettingForm
from django.contrib.sites.shortcuts import get_current_site
from django.utils.encoding import force_bytes, force_text
from django.utils.http import urlsafe_base64_encode, urlsafe_base64_decode
from django.template.loader import render_to_string
from .tokens import account_activation_token
from django.contrib.auth.models import User
from django.core.mail import EmailMessage

from django.http import HttpResponsePermanentRedirect, HttpResponseRedirect
from django.shortcuts import render_to_response

from .forms import AimForm, ListForm, SubAimForm, SubaimParsingForm
from django.core import serializers	

from .models import Setting, Aim, Description, List as ListModel
from .analysis import *
from django.http import JsonResponse
import json

from django.views.generic.list import ListView
from django.core.paginator import Paginator, PageNotAnInteger, EmptyPage
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.views.generic.edit import CreateView, UpdateView, DeleteView
from django.urls import reverse_lazy
from django.urls import reverse
from media.collage import fcollage
import subprocess
import os

from .google_calendar_interaction import calendar_authorization, add_to_calendar

from datetime import datetime
from django.views.decorators.csrf import csrf_exempt

def validate(username, password):
    try:
        user = User.objects.get(username=username)
    except User.DoesNotExist:
        return false
    return user.check_password(password)
        
def api_check_password(request, username):
    if 'HTTP_PASSWORD' not in request.META:
        return HttpResponse('Password is required')
    password = request.META['HTTP_PASSWORD']
    return JsonResponse({'correct' : validate(username, password)})
    
@csrf_exempt
def api_lists(request, username):
    if 'HTTP_PASSWORD' not in request.META or not validate(username, request.META['HTTP_PASSWORD']):
        return HttpResponse(status=404)
    
    if request.method == 'GET':
        data = serializers.serialize('json', ListModel.objects.filter(user_name=username),
                                    ensure_ascii=False, indent=2)
        return HttpResponse(data, content_type='application/json')
    
    if request.method == 'POST':
        fields = json.loads(request.body.decode('utf-8'))
        new_list = ListModel(name=fields['name'], user_name=username)
        new_list.save()
        response = serializers.serialize('json', [new_list], ensure_ascii=False, indent=2)[2:-2]
        return HttpResponse(response)
    
        
@csrf_exempt
def api_aims(request, username, listid):
    if 'HTTP_PASSWORD' not in request.META or not validate(username, request.META['HTTP_PASSWORD']):
        return HttpResponse(status=404)
    
    if request.method == 'GET':
        data = serializers.serialize('json', Aim.objects.filter(user_name=username, list_id=int(listid), parent_id=-1),
                                    ensure_ascii=False, indent=2)
        return HttpResponse(data, content_type='application/json')
    
    if request.method == 'POST':
        fields = json.loads(request.body.decode('utf-8'))
            
        aim = Aim(user_name=username, list_id=listid, parent_id=fields['parent_id'], name=fields['name'],
            deadline=datetime.strptime(fields['date'] + ' ' + fields['time'], '%Y-%m-%d %H:%M:%S'))
        aim.save()
        aim = Aim.objects.filter(user_name=username).last()
        descr = Description(aim_id=aim.id, text=fields['description'])
        descr.save()
        response = serializers.serialize('json', [aim], ensure_ascii=False, indent=2)[2:-2]
        return HttpResponse(response)
    
    if request.method == 'DELETE':
        try:
            to_delete = ListModel.objects.get(pk=listid)
            to_delete.delete()
            response = serializers.serialize('json', [to_delete], ensure_ascii=False, indent=2)[2:-2]
            return HttpResponse(response)
        except ListModel.DoesNotExist:
            return HttpResponse(status=404)
        

@csrf_exempt
def api_aim(request, username, listid, aimid):
    
    if 'HTTP_PASSWORD' not in request.META or not validate(username, request.META['HTTP_PASSWORD']):
            return HttpResponse(status=404)
    try:
        aim = Aim.objects.get(pk=aimid)
    except Aim.DoesNotExist:
        return HttpResponse(status=404)
        
    if request.method == 'GET':        
        data = serializers.serialize('json', [aim], ensure_ascii=False, indent=2)
        subaims = serializers.serialize('json', Aim.objects.filter(parent_id=aimid),
                                        ensure_ascii=False, indent=2)
        descr = serializers.serialize('json', Description.objects.filter(aim_id=aimid))
        aim_info = json.loads(data[2:-2])
        subaims_info = json.loads(subaims)
        description = json.loads(descr)
        aim_info['subaims'] = subaims_info
        aim_info['fields']['description'] = description[0] if description else {"fields" : {"text": ""}}
        return JsonResponse(aim_info)
    
    response = serializers.serialize('json', [aim], ensure_ascii=False, indent=2)[2:-2]
    if request.method == 'POST':
        if not request.body:
            aim.is_completed = not aim.is_completed
            aim.save()
            return HttpResponse(response)
            
        fields = json.loads(request.body.decode('utf-8'))
        aim.name = fields['name']
        aim.deadline = datetime.strptime(fields['date'] + ' ' + fields['time'], '%Y-%m-%d %H:%M:%S')
        try:
            descr = Description.objects.get(aim_id=aimid)
            descr.text = fields['description']
            descr.save()
        except Description.DoesNotExist:
            descr = Description(aim_id=aimid, text=fields['description'])
            descr.save()
        aim.save()
        return HttpResponse(response)
    
    if request.method == 'DELETE':
        aim.delete()
        return HttpResponse(response)

def download_wallpaper(request):
    return render(request, 'wallpaper.html')

def index(request):
    return render(request, 'index.html')
	
def signup(request):
    if request.method == 'POST':
        form = SignupForm(request.POST)
        if form.is_valid():
            user = form.save(commit=False)
            user.is_active = False
            user.save()
            setting = Setting.objects.create(user_name=user.username)
#            setting.save()
            current_site = get_current_site(request)
            mail_subject = 'Активация аккаунта - AchieveMe'
            message = render_to_string('acc_active_email.html', {
                'user': user,
                'domain': current_site.domain,
                'uid':urlsafe_base64_encode(force_bytes(user.pk)).decode(),
                'token':account_activation_token.make_token(user),
            })
            to_email = form.cleaned_data.get('email')
            email = EmailMessage(
                        mail_subject, message, to=[to_email]
            )
            email.send()
            return render(request, 'signup_complete.html', {'form':form})
    else:
        form = SignupForm()
    return render(request, 'signup.html', {'form': form})

def profile_redirect(request):
    return HttpResponsePermanentRedirect("/profile/")
    
def redirect_to_aim(request, username, listid, aimid):
    return HttpResponsePermanentRedirect("/"+username+"/lists/"+listid+'/'+aimid)
    
def redirect_to_aimlist(request, username, listid):
    return HttpResponsePermanentRedirect("/"+username+"/lists/"+listid)

def redirect_to_list(request, username):
    return HttpResponsePermanentRedirect("/"+username+"/lists/")

def activate(request, uidb64, token):
    try:
        uid = force_text(urlsafe_base64_decode(uidb64))
        user = User.objects.get(pk = uid)
    except(TypeError, ValueError, OverflowError, User.DoesNotExist):
        user = None
    if user is not None and account_activation_token.check_token(user, token):
        user.is_active = True
        user.save()
        login(request, user)
        return render(request, 'activation_complete.html')
    else:
        return HttpResponse('Activation link is invalid!')
		
def profile(request):
    fcollage()
    return render(request, 'profile.html')

def AimListView(request, username):
    lists = ListModel.objects.filter(user_name = username)
    vars = dict(
        lists = lists,
        ListAddingForm = ListForm(),
        list_link = "/"+username+"/lists/"
        )
    if request.method == 'POST':
        form = ListForm(request.POST)
        if form.is_valid():
            list = form.save(commit = False)
            list.user_name = request.user.username
            list.save()
            return HttpResponseRedirect("/"+username+"/lists/red_to_list")
    else:
        form = ListForm()
    vars['ListAddingForm'] = form
    return render(request, 'lists.html', vars)
    
def AimView(request, username, listid):
    lists = ListModel.objects.filter(user_name = username)
    aims = Aim.objects.filter(user_name = username, list_id = listid, parent_id = -1, is_completed = False)
    completed = Aim.objects.filter(user_name = username, list_id = listid, parent_id = -1, is_completed = True)
    list = ListModel.objects.get(id = listid)
    vars = dict(
        lists = lists,
        aims = aims,
        listname = list.name,
        formA = AimForm(),
        formB = ListForm(),
        list_link = "/"+username+"/lists/",
        completed = completed,
        ListAddingForm = ListForm()
        )

    if request.method == 'POST' and 'aimbtn' in request.POST:
        form = AimForm(request.POST, request.FILES)
        if form.is_valid():
            aim = form.save(commit = False)
            aim.user_name = username
            list = ListModel.objects.get(id = listid)
            aim.list_id= list.id
            aim.save()
            setting = Setting.objects.get(user_name = username)
            if setting.google_sync:
                add_to_calendar(aim, setting.Gmt)
            return HttpResponseRedirect("/"+username+"/lists/"+listid+"/red_to_aimlist")
    else:
        form = AimForm()
        
    if request.method == 'POST':   
        form = ListForm(request.POST)
        if form.is_valid():
            list = form.save(commit = False)
            list.user_name = request.user.username
            list.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+"/red_to_aimlist")
    else:
        form = ListForm()

    return render(request, 'aims.html', vars)

def editSubAimView(request, username, listid, aimid, pk):
    parent = Aim.objects.get(id = aimid)
    lists = ListModel.objects.filter(user_name = username)
    list = ListModel.objects.get(id = listid)
    subaims = Aim.objects.filter(parent_id = aimid, is_completed = False)
    cur_subaim = Aim.objects.get(id = pk)
    
    vars = dict(
        subaims = subaims,
        lists = lists,
        aim = parent,
        listname = list.name,
        formA = SubAimForm(),
        formB = ListForm(),
        list_link = "/"+username+"/lists/",
        ListAddingForm = ListForm()
        )

    if request.method == 'POST' and 'aimbtn' in request.POST:
        form = SubAimForm(request.POST, request.FILES)
        if form.is_valid():
            cur_subaim.user_name = username
            cur_subaim.name = form.cleaned_data['name']
            cur_subaim.deadline = form.cleaned_data['deadline']
            cur_subaim.is_important = form.cleaned_data['is_important']
            cur_subaim.is_remind = form.cleaned_data['is_remind']
            list = ListModel.objects.get(id = listid)
            cur_subaim.list_id = listid
            cur_subaim.parent_id = aimid
            cur_subaim.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    else:
        form = SubAimForm(instance = Aim.objects.get(id = pk))
	
    vars['formA'] = form
    return render(request, 'edit_subaim.html', vars)

class deleteListView(DeleteView):
    model = ListModel
    form_class = ListForm
    
    def get_success_url(self):
        list = ListModel.objects.get(id = self.object.id)
        return reverse('lists', kwargs={'username': list.user_name})
    
class deleteSubAimView(DeleteView):
    model = Aim
    form_class = SubAimForm
    
    def get_success_url(self):
        parent = Aim.objects.get(id = self.object.parent_id)
        return reverse ('subaim', kwargs={'username': parent.user_name, 'listid': parent.list_id, 'aimid': parent.id})
    
    def get(self, request, *args, **kwargs):
        return self.post(request, *args, **kwargs)

def editListView(request, username, pk):
    lists = ListModel.objects.filter(user_name = username)
    cur_list = ListModel.objects.get(id = pk)
    vars = dict(
        lists = lists,
        form = ListForm(),
        list_link = "/"+username+"/lists/",
        ListAddingForm = ListForm()
        )
    if request.method == 'POST':
        form = ListForm(request.POST)
        if form.is_valid():
            cur_list.user_name = request.user.username
            cur_list.name = form.cleaned_data['name']
            cur_list.save()
            return HttpResponseRedirect("/"+username+"/lists/red_to_list")
    else:
        form = ListForm(instance = ListModel.objects.get(id = pk))
 
    vars['form'] = form
    return render(request, 'edit_list.html', vars)

def editAimView(request, username, listid, pk):
    lists = ListModel.objects.filter(user_name = username)
    aims = Aim.objects.filter(user_name = username, list_id = listid, parent_id = -1, is_completed = False)
    list = ListModel.objects.get(id = listid)
    cur_aim = Aim.objects.get(id = pk)
    vars = dict(
        lists = lists,
        aims = aims,
        listname = list.name,
        formA = AimForm(),
        formB = ListForm(),
        list_link = "/"+username+"/lists/",
        ListAddingForm = ListForm()
        )

    if request.method == 'POST' and 'aimbtn' in request.POST:
        form = AimForm(request.POST, request.FILES)
        if form.is_valid():
            cur_aim.user_name = username
            cur_aim.name = form.cleaned_data['name']
            cur_aim.deadline = form.cleaned_data['deadline']
            cur_aim.is_important = form.cleaned_data['is_important']
            cur_aim.is_remind = form.cleaned_data['is_remind']
            cur_aim.image = form.cleaned_data['image']
            list = ListModel.objects.get(id = listid)
            cur_aim.list_id= list.id
            cur_aim.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+"/red_to_aimlist")
    else:
        form = AimForm(instance = Aim.objects.get(id = pk))
        
    vars['formA'] = form
    return render(request, 'edit_aim.html', vars)
    
def completeAimView(request, username, listid, aimid):
    cur_aim = Aim.objects.get(id = aimid)
    cur_aim.is_completed = True
    cur_aim.save()
    
    return HttpResponseRedirect("/"+username+"/lists/"+listid+"/red_to_aimlist")
    
def completeSubAimView(request, username, listid, aimid, subaim_id):
    cur_aim = Aim.objects.get(id = subaim_id)
    cur_aim.is_completed = True
    cur_aim.save()
    
    return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    
def cancel_completeSubAimView(request, username, listid, aimid, subaim_id):
    cur_aim = Aim.objects.get(id = subaim_id)
    cur_aim.is_completed = False
    cur_aim.save()
    
    return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    
def cancel_completeAimView(request, username, listid, aimid):
    cur_aim = Aim.objects.get(id = aimid)
    cur_aim.is_completed = False
    cur_aim.save()
    
    return HttpResponseRedirect("/"+username+"/lists/"+listid+"/red_to_aimlist")
    
class deleteAimView(DeleteView):
    model = Aim
    form_class = AimForm
    
    def get_success_url(self):
        parent = Aim.objects.get(id = self.object.id)
        return reverse ('aims', kwargs={'username': parent.user_name, 'listid': parent.list_id})
    
    def get(self, request, *args, **kwargs):
        return self.post(request, *args, **kwargs)

def SubAimView(request, username, listid, aimid):
    parent = Aim.objects.get(id = aimid)
    lists = ListModel.objects.filter(user_name = username)
    list = ListModel.objects.get(id = listid)
    subaims = Aim.objects.filter(parent_id = aimid, is_completed = False).order_by('deadline')
    completed = Aim.objects.filter(parent_id = aimid, is_completed = True).order_by('deadline')
    vars = dict(
        subaims = subaims,
        lists = lists,
        aim = parent,
        listname = list.name,
        formA = SubAimForm(),
        formB = ListForm(),
        formC = SubaimParsingForm(),
        list_link = "/"+username+"/lists/",
        completed = completed,
        ListAddingForm = ListForm()
        )

    if request.method == 'POST' and 'aimbtn' in request.POST:
        formA= SubAimForm(request.POST, request.FILES)
        if formA.is_valid():
            aim = formA.save(commit = False)
            aim.user_name = username
            list = ListModel.objects.get(id = listid)
            aim.list_id = listid
            aim.parent_id = aimid
            aim.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    else:
        formA = SubAimForm()
        
    if request.method == 'POST' and 'parsebtn' in request.POST:
        formC = SubaimParsingForm(request.POST, request.FILES)
        if formC.is_valid():
            text = formC.cleaned_data['text'].split('\n')
            for row in text:
                name, deadline = goal_analysis(row)
                aim = Aim(name = name, deadline = deadline, user_name = username, list_id = listid, parent_id = aimid)
                aim.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    else:
        formC = SubaimParsingForm()
        
    if request.method == 'POST':   
        formB = ListForm(request.POST)
        if formB.is_valid():
            list = formB.save(commit = False)
            list.user_name = request.user.username
            list.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    else:
        formB = ListForm()

    vars['formA'] = formA
    vars['formB'] = formB
    vars['formC'] = formC
    return render(request, 'deep_aim.html', vars)

def settings(request, username):
    if request.method == 'POST':
        form = SettingForm(request.POST)
        if form.is_valid():
            setting = form.save(commit = False)
            setting.user_name = username
            Setting.objects.get(user_name = username).delete()
            setting.save()
            if setting.google_sync:
                calendar_authorization(username)

    else:
        form = SettingForm(instance = Setting.objects.get(user_name = username))
    return render(request, 'settings.html', {'form': form})


	

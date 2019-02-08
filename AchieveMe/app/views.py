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

from .forms import AimForm, ListForm, SubAimForm
from django.core import serializers	

from .models import Setting, Aim, List as ListModel

from django.http import JsonResponse
import json

from django.views.generic.list import ListView
from django.core.paginator import Paginator, PageNotAnInteger, EmptyPage
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.views.generic.edit import CreateView, UpdateView, DeleteView
from django.urls import reverse_lazy
from django.urls import reverse


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
    
def api_lists(request, username):
    if 'HTTP_PASSWORD' not in request.META or not validate(username, request.META['HTTP_PASSWORD']):
        return HttpResponse(status=404)
    
    data = serializers.serialize('json', ListModel.objects.filter(user_name=username),
                                ensure_ascii=False, indent=2)
    return HttpResponse(data, content_type='application/json')

def api_aims(request, username, listid):
    if 'HTTP_PASSWORD' not in request.META or not validate(username, request.META['HTTP_PASSWORD']):
        return HttpResponse(status=404)
    
    data = serializers.serialize('json', Aim.objects.filter(user_name=username, list_id=int(listid), parent_id=-1),
                                 ensure_ascii=False, indent=2)
    return HttpResponse(data, content_type='application/json')

def api_aim(request, username, listid, aimid):
    if 'HTTP_PASSWORD' not in request.META or not validate(username, request.META['HTTP_PASSWORD']):
        return HttpResponse(status=404)
    
    try:
        aim = Aim.objects.get(pk=aimid)
    except Aim.DoesNotExist:
        return HttpResponse(status=404)
    
    data = serializers.serialize('json', [aim], ensure_ascii=False, indent=2)
    subaims = serializers.serialize('json', Aim.objects.filter(parent_id=aimid),
                                    ensure_ascii=False, indent=2)
    aim_info = json.loads(data[2:-2])
    subaims_info = json.loads(subaims)
    aim_info['subaims'] = subaims_info
    return JsonResponse(aim_info)

def index(request):
    return render(request, 'index.html')
	
def signup(request):
    if request.method == 'POST':
        form = SignupForm(request.POST)
        if form.is_valid():
            user = form.save(commit=False)
            user.is_active = False
            user.save()
            setting = Setting(user_name=request.user.username)
            setting.save()
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
    
def redirect_to_subaim(request, username, listid, aimid):
    return HttpResponsePermanentRedirect("/"+username+"/lists/"+listid + '/' + aimid)
    
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
    return render(request, 'profile.html')

def AimListView(request, username):
    lists = ListModel.objects.filter(user_name = username)
    vars = dict(
        lists = lists,
        form = ListForm(),
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
 
    return render(request, 'lists.html', vars)
    
def AimView(request, username, listid):
    lists = ListModel.objects.filter(user_name = username)
    aims = Aim.objects.filter(user_name = username, list_id = listid, parent_id = -1)
    list = ListModel.objects.get(id = listid)
    vars = dict(
        lists = lists,
        aims = aims,
        listname = list.name,
        formA = AimForm(),
        formB = ListForm(),
        list_link = "/"+username+"/lists/"
        )

    if request.method == 'POST' and 'aimbtn' in request.POST:
        form = AimForm(request.POST, request.FILES)
        if form.is_valid():
            aim = form.save(commit = False)
            aim.user_name = username
            list = ListModel.objects.get(id = listid)
            aim.list_id= list.id
            aim.save()
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
    subaims = Aim.objects.filter(parent_id = aimid)
    cur_subaim = Aim.objects.get(id = pk)
    
    vars = dict(
        subaims = subaims,
        lists = lists,
        aim = parent,
        listname = list.name,
        formA = SubAimForm(),
        formB = ListForm(),
        list_link = "/"+username+"/lists/"
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
            return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_subaim")
    else:
        form = SubAimForm(instance = Aim.objects.get(id = pk))
	
    vars['formA'] = form
    return render(request, 'app/aim_form.html', vars)
    
class deleteSubAimView(DeleteView):
    model = Aim
    form_class = SubAimForm
    
    def get_success_url(self):
        parent = Aim.objects.get(id = self.object.parent_id)
        return reverse ('subaim', kwargs={'username': parent.user_name, 'listid': parent.list_id, 'aimid': parent.id})
    
    def get(self, request, *args, **kwargs):
        return self.post(request, *args, **kwargs)

def SubAimView(request, username, listid, aimid):
    parent = Aim.objects.get(id = aimid)
    lists = ListModel.objects.filter(user_name = username)
    list = ListModel.objects.get(id = listid)
    subaims = Aim.objects.filter(parent_id = aimid)
    vars = dict(
        subaims = subaims,
        lists = lists,
        aim = parent,
        listname = list.name,
        formA = SubAimForm(),
        formB = ListForm(),
        list_link = "/"+username+"/lists/"
        )

    if request.method == 'POST' and 'aimbtn' in request.POST:
        form = SubAimForm(request.POST, request.FILES)
        if form.is_valid():
            aim = form.save(commit = False)
            aim.user_name = username
            list = ListModel.objects.get(id = listid)
            aim.list_id = listid
            aim.parent_id = aimid
            aim.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    else:
        form = SubAimForm()
        
    if request.method == 'POST':   
        form = ListForm(request.POST)
        if form.is_valid():
            list = form.save(commit = False)
            list.user_name = request.user.username
            list.save()
            return HttpResponseRedirect("/"+username+"/lists/"+listid+'/'+aimid+"/red_to_aim")
    else:
        form = ListForm()

    return render(request, 'deep_aim.html', vars)

def settings(request):
    if request.method == 'POST':
        form = SettingForm(request.POST)
        if form.is_valid():
            setting = form.save(commit = False)
            setting.save()
    else:
        form = SettingForm()
    return render(request, 'settings.html', {'form': form})


	

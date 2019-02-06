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

from .forms import AimForm, ListForm
from django.core import serializers	

from .models import Setting, Aim, List as ListModel

from django.http import JsonResponse
import json

from django.views.generic.list import ListView
from django.core.paginator import Paginator, PageNotAnInteger, EmptyPage
from django.shortcuts import render_to_response
from django.template import RequestContext


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
    if 'HTTP_PASSWORD' not in request.META:
        return HttpResponse('Password is required')
    response = serializers.serialize('json', List.objects.filter(user_name=username),
                                     fields=('name'), ensure_ascii=False, indent=2)
    return HttpResponse(response, content_type='application/json')

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

def add_aim(request, username, listid):
    attachment = {'form': AimForm()}
    if request.method == 'POST':
        form = AimForm(request.POST)
        if form.is_valid():
            aim = form.save(commit = False)
            aim.user_name = username
            list = ListModel.objects.get(id = listid)
            aim.list_id= list.id
            aim.save()
            attachment['saved'] = True
            render(request, 'add_aim.html', attachment)
    else:
        form = AimForm()

    return render(request, 'add_aim.html', attachment)
    
def add_list(request):
    attachment = {'form': ListForm()}
    if request.method == 'POST':
        form = ListForm(request.POST)
        if form.is_valid():
            list = form.save(commit = False)
            list.user_name = request.user.username
            list.save()
            attachment['saved'] = True
            render(request, 'add_list.html', attachment)
    else:
        form = ListForm()

    return render(request, 'add_list.html', attachment)

def AimListView(request, username):
    lists = ListModel.objects.filter(user_name = username)
    vars = dict(
        lists = lists,
        )
    return render(request, 'lists.html', vars)
    
def AimView(request, username, listid):
    aims = Aim.objects.filter(user_name = username, list_id = listid)
    list = ListModel.objects.get(id = listid)
    vars = dict(
        aims = aims,
        listname = list.name
        )
    return render(request, 'aims.html', vars)
    
def AimDeepView(request, username, listid, aimid):
    list = ListModel.objects.get(id = listid)
    aims = Aim.objects.all()
    aim = Aim.objects.get(user_name = username, list_id = listid, id = aimid)
    var = dict(aim = aim, listname = list.name, name = aim.name)
    return render(request, 'deep_aim.html', var)

def settings(request):
    if request.method == 'POST':
        form = SettingForm(request.POST)
        if form.is_valid():
            setting = form.save(commit = False)
            setting.save()
    else:
        form = SettingForm()
    return render(request, 'settings.html', {'form': form})


	

from django.shortcuts import render, redirect
from django.http import HttpResponse

#from django.contrib.auth.base_user.AbstractBaseUser import check_password
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

from .forms import AimForm
from .models import Aims
from django.core import serializers	

from .models import Setting

from django.http import JsonResponse
import json

def validate_login_passw(request):
    if 'HTTP_USERNAME' not in request.META or 'HTTP_PASSWORD' not in request.META:
        return HttpResponse('This is API page for validating username-password pairs')
    Username = request.META['HTTP_USERNAME']
    Password = request.META['HTTP_PASSWORD']
    user = User.objects.get(username=Username);
    return JsonResponse({'valid' : str(user.check_password(Password))})
    
def aims_list(request, username):
    response = serializers.serialize('json', Aims.objects.filter(User_name=username), fields=('Name'), ensure_ascii=False, indent=2)
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
	
def add_aim(request):
    if request.method == 'POST':
        form = AimForm(request.POST)
        if form.is_valid():
            profile = form.save(commit = False)
            profile.User_name = request.user.username
            profile.save()
            return HttpResponse('Цель добавлена')
    else:
        form = AimForm()

    return render(request, 'add_aim.html', {'form': form})


def settings(request):
    if request.method == 'POST':
        form = SettingForm(request.POST)
        if form.is_valid():
            setting = form.save(commit = False)
            setting.save()
    else:
        form = SettingForm()
    return render(request, 'settings.html', {'form': form})


	

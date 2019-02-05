from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from .models import Aims
from .models import Setting
from django.db import models

class SignupForm(UserCreationForm):
    email = forms.EmailField(max_length=200, help_text='Required')
    class Meta:
        model = User
        fields = ('username', 'email', 'password1', 'password2')
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields['username'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Логин', 'maxlength': '15'})
        self.fields['password1'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Пароль'})
        self.fields['password2'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Повторите пароль'})
        self.fields['email'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'E-mail'})
			
			
class AimForm(forms.ModelForm):
    Name = models.CharField(max_length=120),
    #User_name = models.CharField(max_length=120)
    class Meta:
        model = Aims
        fields = ('Name',)

class SettingForm(forms.ModelForm):
    class Meta:
        model = Setting
        fields = ('Gmt', 'is_notification_to_email')
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields['Gmt'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Часовой пояс'})
        self.fields['is_notification_to_email'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Высылать уведомления'})

from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from .models import Setting, Aim, List
from django.db import models

class SignupForm(UserCreationForm):
    email = forms.EmailField(max_length = 200, help_text = 'Required')
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
    class Meta:
        model = Aim
        fields = ('name', 'deadline', 'is_important', 'is_remind', 'time_to_do', 'image')

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
        self.fields['name'].label = 'Цель:'
        self.fields['deadline'].label = 'Дедлайн:'
        self.fields['is_important'].label = 'Важное:'
        self.fields['is_remind'].label = 'Уведомление:'
        self.fields['time_to_do'].label = 'Время на выполнение:'
        self.fields['image'].label = 'Аватар:'

        self.fields['name'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Название цели', 'maxlength': '120'})
        self.fields['deadline'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Дата и время', 'maxlength': '120'})
        self.fields['is_important'].widget.attrs.update(
            {'class':'.mark', 'placeholder': 'Важное ли?', 'maxlength': '1'})
        self.fields['is_remind'].widget.attrs.update(
            {'class': 'custom-control-label', 'placeholder': 'Нужно напоминание?', 'maxlength': '200'})
        self.fields['time_to_do'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Сколько времени нужно на выполнение? (в минутах)', 'maxlength': '120'})
        self.fields['image'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Загрузите изображение', 'maxlength': '120'})
            
class ListForm(forms.ModelForm):
    class Meta:
        model = List
        fields = ('name',)
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields['name'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Название списка', 'maxlength': '120'})
	
class SettingForm(forms.ModelForm):
    class Meta:
        model = Setting
        fields = ('Gmt', 'is_notification_to_email')
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields['Gmt'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Часовой пояс'})
        self.fields['is_notification_to_email'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Высылать уведомления', 'style' : 'width:20px;height:20px;'})
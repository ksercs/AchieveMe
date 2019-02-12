from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from .models import Setting, Aim, List, Text
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
        fields = ('name', 'deadline', 'is_important', 'image', 'cur_points', 'all_points')

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
        self.fields['name'].label = 'Цель:'
        self.fields['deadline'].label = 'Дедлайн:'
        self.fields['is_important'].label = 'Важное:'
        self.fields['image'].label = 'Аватар:'
        self.fields['cur_points'].label = 'Готово:'
        self.fields['all_points'].label = 'Всего:'
        self.fields['cur_points'].widget = forms.NumberInput(attrs={'style': 'width:70%;float:right;line-height:23px;', 'class':'col-lg-2'})
        self.fields['all_points'].widget = forms.NumberInput(attrs={'style': 'width:70%;float:right;line-height:23px;', 'class':'col-lg-2'})

        self.fields['name'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Название цели', 'maxlength': '120'})
        self.fields['deadline'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Дата и время', 'maxlength': '120'})
        self.fields['is_important'].widget.attrs.update(
            {'class':'.mark', 'placeholder': 'Важное ли?', 'maxlength': '1'})
            
class SubAimForm(forms.ModelForm):
    class Meta:
        model = Aim
        fields = ('name', 'deadline', 'is_important')

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
        self.fields['name'].label = 'Подцель:'
        self.fields['deadline'].label = 'Дедлайн:'
        self.fields['is_important'].label = 'Важное:'

        self.fields['name'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Новая подцель', 'maxlength': '120'})
        self.fields['deadline'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Дата и время', 'maxlength': '120'})
        self.fields['is_important'].widget.attrs.update(
            {'class':'.mark', 'placeholder': 'Важное ли?', 'maxlength': '1'})
        
class SubaimParsingForm(forms.ModelForm):
    class Meta:
        model = Text
        fields = ('text',)
        
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
        self.fields['text'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': """Планируй достижение цели с удобством!
                                                                    Пишите названия подцелей вместе с дедлайнами по одному на строку и подцели создадутся автоматически!""", 'maxlength': '1000'})
        
class ListForm(forms.ModelForm):
    class Meta:
        model = List
        fields = ('name',)
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields['name'].widget.attrs.update(
            {'class': 'form-control', 'placeholder': 'Новый список', 'maxlength': '120'})
	
class SettingForm(forms.ModelForm):
    class Meta:
        model = Setting
        fields = ('google_sync',)
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
        self.fields['google_sync'].label = 'Синхронизация с google-календарем:'
        
        self.fields['google_sync'].widget.attrs.update(
            {'class': 'form-horizontal', 'placeholder': 'Синхронизация', 'style' : 'width:20px;height:20px;'})
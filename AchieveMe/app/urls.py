from django.urls import include, path
from django.conf.urls import url

from . import views

from django.http import HttpResponsePermanentRedirect

urlpatterns = [
    path('', views.index, name='index'),
	path('', include('django.contrib.auth.urls')),
	path('profile/', views.profile, name='profile'),
	
	url(r'^signup/$', views.signup, name='signup'),
    url(r'^activate/(?P<uidb64>[0-9A-Za-z_\-]+)/(?P<token>[0-9A-Za-z]{1,13}-[0-9A-Za-z]{1,20})/$',
        views.activate, name='activate'),
		
	url(r'^accounts/profile/$', views.profile_redirect, name='url_redirect'),
	url(r'^add_aim/$', views.add_aim, name='add_aim')
]
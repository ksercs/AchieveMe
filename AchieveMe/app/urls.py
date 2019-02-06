from django.urls import include, path
from django.conf.urls import url

from . import views

from django.http import HttpResponsePermanentRedirect

urlpatterns = [
    path('', views.index, name='index'),
	path('', include('django.contrib.auth.urls')),
	path('profile/', views.profile, name='profile'),
	path('api/login/', views.validate_login_passw, name='validate'),
	
	url(r'^signup/$', views.signup, name='signup'),
    url(r'^activate/(?P<uidb64>[0-9A-Za-z_\-]+)/(?P<token>[0-9A-Za-z]{1,13}-[0-9A-Za-z]{1,20})/$',
        views.activate, name='activate'),
		
	url(r'^accounts/profile/$', views.profile_redirect, name='url_redirect'),
	url(r'^add_list/$', views.add_list, name='add_list'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)$', views.AimDeepView, name='deep_aim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/add_aim$',           views.add_aim, name='add_aim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/$',                        views.AimView, name='aim_lists'),
    url(r'^(?P<username>\w+)/lists/$',                                              views.AimListView, name='lists'),
	url(r'^settings/$', views.settings, name='settings'),	
	url(r'^api/(?P<username>\w+)/aims_list/$', views.aims_list)
]

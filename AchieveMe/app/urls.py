from django.urls import include, path
from django.conf.urls import url

from django.conf.urls.static import static
from django.conf import settings

from . import views

from django.contrib.staticfiles.urls import staticfiles_urlpatterns
from django.http import HttpResponsePermanentRedirect

urlpatterns = [
    path('', views.index, name='index'),
	path('', include('django.contrib.auth.urls')),
	path('profile/', views.profile, name='profile'),
	
	url(r'^signup/$', views.signup, name='signup'),
    url(r'^activate/(?P<uidb64>[0-9A-Za-z_\-]+)/(?P<token>[0-9A-Za-z]{1,13}-[0-9A-Za-z]{1,20})/$',
        views.activate, name='activate'),
	url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/red_to_subaim$', views.redirect_to_subaim, name = 'red_to_subaim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/red_to_aim$', views.redirect_to_aim, name='redirect_to_aim'),
    url(r'^(?P<username>\w+)/lists/red_to_list$', views.redirect_to_list, name='redirect_to_list'),
	url(r'^accounts/profile/$', views.profile_redirect, name='url_redirect'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)$', views.SubAimView, name = 'subaim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/$',                        views.AimView,        name = 'aims'),
    url(r'^(?P<username>\w+)/lists/$',                                              views.AimListView,   name = 'lists'),
	url(r'^settings/$', views.settings, name='settings'),	
	url(r'^api/(?P<username>\w+)/lists/$', views.api_lists),
	url(r'^api/(?P<username>\w+)/check_password/$', views.api_check_password),
	url(r'^api/(?P<username>\w+)/(?P<listid>\d+)/$', views.api_aims),
	url(r'^api/(?P<username>\w+)/(?P<listid>\d+)/(?P<aimid>\d+)/$', views.api_aim)
]

if settings.DEBUG:
    if settings.MEDIA_ROOT:
        urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

urlpatterns += staticfiles_urlpatterns()

from django.urls import include, path
from django.conf.urls import url

from django.conf.urls.static import static
from django.conf import settings

from AchieveMe.settings import STATIC_ROOT
from . import views

from django.contrib.staticfiles.urls import staticfiles_urlpatterns
from django.http import HttpResponsePermanentRedirect
from django.contrib.auth import views as auth_views
from django.views.generic.base import RedirectView


urlpatterns = [
    path('', views.index, name='index'),
	path('', include('django.contrib.auth.urls')),
    
	url(r'^(?P<username>\w+)/profile/$', views.profile, name='profile'),
	url(r'^signup/$', views.signup, name='signup'),
    url(r'^activate/(?P<uidb64>[0-9A-Za-z_\-]+)/(?P<token>[0-9A-Za-z]{1,13}-[0-9A-Za-z]{1,20})/$',
        views.activate, name='activate'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/red_to_aimlist$', views.redirect_to_aimlist, name='redirect_to_aimlist'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/red_to_aim$', views.redirect_to_aim, name='redirect_to_aim'),
    url(r'^(?P<username>\w+)/lists/red_to_list$', views.redirect_to_list, name='redirect_to_list'),
    url(r'^(?P<username>\w+)/lists/(?P<pk>\w+)/edit$', views.editListView, name='edit'),
    url(r'^(?P<username>\w+)/lists/(?P<pk>\w+)/delete$', views.deleteListView.as_view(), name='delete_list'),
	url(r'^accounts/profile/$', views.profile_redirect, name='url_redirect'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/$', views.SubAimView, name = 'subaim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/(?P<pk>\d+)/edit/$', views.editSubAimView, name = 'edit_subaim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/(?P<pk>\d+)/delete/$', views.deleteSubAimView.as_view(), name = 'delete_subaim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<pk>\d+)/edit/$', views.editAimView, name = 'edit_aim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/complete/$', views.completeAimView, name = 'complete_aim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/(?P<subaim_id>\d+)/complete/$', views.completeSubAimView, name = 'complete_subaim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/(?P<subaim_id>\d+)/cancel_complete/$', views.cancel_completeSubAimView, name = 'cancel_complete_subaim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)/cancel_complete/$', views.cancel_completeAimView, name = 'cancel_complete_aim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<pk>\d+)/delete/$', views.deleteAimView.as_view(), name = 'delete_aim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/$',                        views.AimView,        name = 'aims'),
    url(r'^(?P<username>\w+)/lists/$',                                              views.AimListView,   name = 'lists'),
	url(r'^(?P<username>\w+)/settings/$', views.settings, name='settings'),	
	url(r'^api/(?P<username>\w+)/lists/$', views.api_lists),
	url(r'^api/(?P<username>\w+)/check_password/$', views.api_check_password),
	url(r'^api/(?P<username>\w+)/(?P<listid>\d+)/$', views.api_aims),
	url(r'^api/(?P<username>\w+)/(?P<listid>\d+)/(?P<aimid>\d+)/$', views.api_aim),
	url(r'^api/(?P<username>\w+)/(?P<listid>\d+)/analysis/$', views.api_analysis),
	url(r'^api/(?P<username>\w+)/aims/', views.api_user_aims),
	url(r'^api/(?P<username>\w+)/(?P<aimid>\d+)/progress/$', views.api_progress)
]

if settings.DEBUG:
    if settings.MEDIA_ROOT:
        urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

urlpatterns += staticfiles_urlpatterns()

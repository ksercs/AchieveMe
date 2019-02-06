from django.urls import include, path
from django.conf.urls import url

from django.conf.urls.static import static
from django.conf import settings

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
#	url(r'^add_list/$', views.add_list, name='add_list'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/(?P<aimid>\d+)$', views.AimDeepView, name = 'deep_aim'),
#   url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/add_aim$',           views.add_aim,         name = 'add_aim'),
    url(r'^(?P<username>\w+)/lists/(?P<listid>\d+)/$',                        views.AimView,        name = 'aims'),
    url(r'^(?P<username>\w+)/lists/$',                                              views.AimListView,   name = 'lists'),
	url(r'^settings/$', views.settings, name='settings'),	

] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

	url(r'^api/(?P<username>\w+)/aims_list/$', views.api_lists),
	url(r'^api/(?P<username>\w+)/check_password/$', views.check_password)

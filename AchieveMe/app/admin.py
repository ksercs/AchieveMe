from django.contrib import admin

from .models import Aim, List, Setting, Description

admin.site.register(Aim)
admin.site.register(List)
admin.site.register(Setting)
admin.site.register(Description)
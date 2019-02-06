from django.db import models
from django.utils.timezone import now

class Aim(models.Model):
    user_name  = models.CharField       (max_length = 120)
    list_id         = models.IntegerField         (default = -1)
    parrent_id    = models.IntegerField    (default = -1)
    name 		  = models.CharField        (max_length = 120, default ='')
    deadline       = models.DateTimeField(default = now)
    is_important = models.BooleanField  (default = 0)
    is_remind     = models.BooleanField  (default = 0)
    is_made 	   = models.BooleanField  (default = 0)
    time_to_do   = models.IntegerField    ()
	
class List(models.Model):
	name 	     = models.CharField(max_length = 120)
	user_name = models.CharField(max_length = 120)
	
class Setting(models.Model):
	user_name 				   = models.CharField     (max_length = 20)
	is_notification_to_email = models.BooleanField(default = True)
	Gmt						       = models.IntegerField  (default = '+3')
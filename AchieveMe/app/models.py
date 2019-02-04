from django.db import models

class Aims(models.Model):
	#Aim_id, # models.IntegerField()
	#List_id,# = models.IntegerField()
	#цParrentAim_id,# = models.IntegerField()
	Name = models.CharField(max_length=120, default='')
	#Deadline = models.DateTimeField(auto_now_add=True)
	#IsImportant = models.BooleanField(default=0)
	#Remind = models.BooleanField(default=0)
	#TimeToDo = models.IntegerField(default=0)
"""
class User(models.Model):
	UserLogin = models.CharField(max_length=20)
	Password = models.CharField(max_length=20)

class Lists(models.Model):
	ListId = models.IntegerField()
	UserLogin = models.CharField(max_length=20)
    
class Description(models.Model):
	DescriptionId = models.IntegerField()
	AimId = models.IntegerField()
	DescriptionText = models.CharField(max_length=300)

class Comments(models.Model):
	CommentId = models.IntegerField()
	AimId = models.IntegerField()
	CommentText = models.CharField(max_length=300)

class Files(models.Model):
	FileId = models.IntegerField()
	DescriptionId = models.IntegerField()
	CommentId = models.IntegerField()

class Settings(models.Model):
	UserLogin = models.CharField(max_length=20)
	IsNotification = models.BooleanField()
"""
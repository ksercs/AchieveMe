from django.db import models

class User(models.Model):
	UserLogin = models.CharField(max_length=20)
	Password = models.CharField(max_length=20)

class Lists(models.Model):
	ListId = models.IntegerField()
	UserLogin = models.CharField(max_length=20)

class Aims(models.Model):
	AimId = models.IntegerField()
	ListId = models.IntegerField()
	ParrentAimId = models.IntegerField()
	Name = models.CharField(max_length=120)
	Deadline = models.DateTimeField()
	IsImportant = models.BooleanField()
	Remind = models.BooleanField()
	TimeToDo = models.IntegerField()

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
	
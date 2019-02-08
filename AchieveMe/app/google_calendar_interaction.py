from __future__ import print_function
from apiclient import discovery
from httplib2 import Http
from oauth2client import file, client, tools
import datetime
import os
import json

SCOPES = 'https://www.googleapis.com/auth/calendar'
from .models import Aim

try:
	import argparse
	flags = tools.argparser.parse_args([])
except ImportError:
	flags = None

def calendar_authorization(username):
	store = open('app/static/secret_data/' + username +'.json', 'w')
	store.close()
	store = file.Storage('app/static/secret_data/' + username +'.json')
	creds = store.get()
	if not creds or creds.invalid:
		flow = client.flow_from_clientsecrets('app/client_secret.json', SCOPES)
		creds = tools.run_flow(flow, store, flags)
	 #discovery.build('calendar', 'v3', http=creds.authorize(Http()))


def add_to_calendar(aim, Gmt):
	store = file.Storage('app/static/secret_data/' + aim.user_name +'.json')
	creds = store.get()
	if not creds or creds.invalid:
		flow = client.flow_from_clientsecrets('app/client_secret.json', SCOPES)
		creds = tools.run_flow(flow, store, flags)
	GCAL = discovery.build('calendar', 'v3', http=creds.authorize(Http()))
	start_time = aim.deadline - datetime.timedelta(minutes=aim.time_to_do)
	EVENT = {
		'summary': aim.name,
		'start': {'dateTime': str(start_time).replace(' ', 'T')},
		'end': {'dateTime': str(aim.deadline).replace(' ', 'T')},
	}
	print(str(start_time).replace(' ', 'T'))
	print(str(aim.deadline).replace(' ', 'T'))

	e = GCAL.events().insert(calendarId='primary', sendNotifications=True, body=EVENT).execute()
	print(e)
	print('''*** %r event added:
    	Start: %s
    	End:   %s''' % (e['summary'].encode('utf-8'),
        e['start']['dateTime'], e['end']['dateTime']))
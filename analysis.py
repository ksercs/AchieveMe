import datetime
from num2words import num2words
import pymorphy2

def number_in_digits(s):
    nums_letters = ['сорок', 'тридцать', 'семь', 'восемнадцать', 'пятьдесят', 'семьдесят', 'девяносто', 'десять', 'семнадцать', 'девятьсот', 'восемьдесят', 'шестьдесят', 'девятнадцать', 'девять', 'шестнадцать', 'двенадцать', 'двадцать', 'пятнадцать', 'пять', 'одиннадцать', 'пятьсот', 'восемь', 'тринадцать', 'семьсот', 'два', 'шесть', 'ноль', 'шестьсот', 'восемьсот', 'двести', 'четыреста', 'четыре', 'один', 'три', 'четырнадцать', 'триста', 'сто', 'тысяча']
    nums_digits = [40, 30, 7, 18, 50, 70, 90, 10, 17, 90, 80, 60, 19, 9, 16, 12, 20, 15, 5, 11, 500, 8, 13, 700, 2, 6, 0, 600, 800, 200, 400, 4, 1, 3, 14, 300, 100, 1000]
    words = list(s.split())
    morph = pymorphy2.MorphAnalyzer()
    for i in range(len(words)):
        w = morph.parse(words[i])[0]
        if (w.normal_form.lower() in nums_letters):
            words[i] = w.normal_form
    summ = 0
    summ_end = 0
    stack = []
    flag = 0
    for i in range(len(words)):
        word = words[i]
        if word in nums_letters:
            ind = nums_letters.index(word)
            summ += nums_digits[ind]
            flag = 1
            continue
        if word == "тысяча":
            if summ == 0:
                summ = 1
            summ_end += summ * 1000
            summ = 0
            flag = 1
            continue
        if word.lower() not in nums_letters:
            summ_end += summ
            if flag:
                stack += [summ_end]
                flag = 0
            summ = 0
            summ_end = 0
    new_words = []
    flag = 0
    p = 0
    for i in range(len(words)):
        if (words[i].lower() in nums_letters) or (words[i] == "тысяча"):
            if flag == 0:
                new_words += [str(stack[p])]
                p += 1
                flag = 1
        else:
            new_words += [words[i]]
            flag = 0
    return new_words
    
        
        
            
        

def goal_analysis(s):
    now = datetime.datetime.now()
    words = number_in_digits(s)
    N = len(words)
    # указываю текущее время
    month = now.month
    day = now.day
    year = now.year
    minute = now.minute
    day_week = datetime.datetime.today().weekday()
    mas_month = ["января", "февраля",	"марта", 
           "апреля", "мая", "июня",
           "июля", "августа", "сентября",
           "октября", "ноября",	"декабря"]
    mas_days = ["понедельнику", "вторнику", "среде", "четвергу", "пятнице", "субботе", "воскресенью"]
    mas_days2 = ["понедельника", "вторника", "среды", "четверга", "пятницы", "субботы", "воскресенья"]
    stack = []
    for i in range(N):
        if words[i].lower() == "завтра":
            day += 1
            stack += [i]
        if words[i].lower() == "послезавтра":
            day += 1
            stack += [i]
        if words[i].lower() == "к" or words[i].lower() == "ко"  or words[i].lower() == "до":
            stack += [i]
            # до завтра
            if words[i + 1].lower() == "завтра":
                day += 1
            # к первому декабря
            if words[i + 1].isdigit() and words[i + 2].lower() in mas_month:
                stack += [i + 1]
                stack += [i + 2]
                day = int(words[i])
                month = mas_month.index(words[i + 2]) + 1
            # к понедельнику
            if words[i + 1].lower() in mas_days or words[i + 1].lower() in mas_days2:
                stack += [i + 1]
                if words[i + 1].lower() in mas_days:
                    while (day_week != mas_days.index(words[i + 1])):
                        day_week = (day_week + 1) % 7
                        day += 1
                if words[i + 1].lower() in mas_days2:
                    while (day_week != mas_days2.index(words[i + 1])):
                        day_week = (day_week + 1) % 7
                        day += 1
            # ко следующей неделе
            if i + 2 < N and words[i + 2].lower() == "недели" and words[i + 1].lower() == "следующей":
                stack += [i + 1]
                stack += [i + 2]
                if words[i + 2].lower() in mas_days:
                    while (day_week != mas_days.index(words[i + 1])):
                        day_week = (day_week + 1) % 7
                        day += 1
                if words[i + 2].lower() in mas_days2:
                    while (day_week != mas_days2.index(words[i + 1])):
                        day_week = (day_week + 1) % 7
                        day += 1            
            # к следующему месяцу
            if words[i + 1].lower() in mas_days:
                ...
                
        if words[i].lower() == "через":
            stack += [i]
            # через 2 дня/недели/месяца/года
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "дня" or words[i + 2].lower() == "дней"):
                stack += [i + 1]
                stack += [i + 2]
                day += int(words[i + 1])
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "недель" or words[i + 2].lower() == "недели"):
                stack += [i + 1]
                stack += [i + 2]                
                day += int(words[i + 1]) * 7
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "месяца" or words[i + 2].lower() == "месяцев"):
                stack += [i + 1]
                stack += [i + 2]                
                month += int(words[i + 1])
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "год" or words[i + 2].lower() == "лет"):
                stack += [i + 1]
                stack += [i + 2]                
                year += int(words[i + 1])
            # через день/неделю/месяц/год
            if i + 2 < N and (words[i + 1].lower() == "день"):
                stack += [i + 1]               
                day += 1
            if i + 2 < N and words[i + 1].lower() == "неделю":
                stack += [i + 1] 
                day += 7
            if i + 2 < N and words[i + 1].lower() == "месяц":
                stack += [i + 1] 
                month += 1
            if i + 2 < N and words[i + 1].lower() == "год":
                stack += [i + 1] 
                year += 1
    goal = []
    for i in range(len(words)):
        if i not in stack:
            goal += [words[i]]
    print("Дедлайн:", year, month, day)
    print("Цель:", ' '.join(goal), '\n')
           
goal_analysis("хочу научиться плавать к понедельнику")
goal_analysis("До вторника надо навестить бабушку")
goal_analysis("Через 10 дней необходимо сдать доклад")
goal_analysis("До завтра выброшу мусор")
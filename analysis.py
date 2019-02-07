import datetime
import pymorphy2
import calendar

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
    month = 0
    day = 0
    year = 0
    day_week = datetime.datetime.today().weekday()
    start_month = 0
    specific_date = 0
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
            # до завтра
            if words[i + 1].lower() == "завтра":
                day += 1
                stack += [i]
            # к первому декабря
            if words[i + 1].isdigit() and words[i + 2].lower() in mas_month:
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                day = int(words[i + 1])
                month = mas_month.index(words[i + 2]) + 1
                specific_date = 0
                
            # к понедельнику
            if words[i + 1].lower() in mas_days or words[i + 1].lower() in mas_days2:
                stack += [i]
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
            if i + 2 < N and words[i + 2].lower() == "неделe" and words[i + 1].lower() == "следующей":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                while (day_week != 0):
                    day_week = (day_week + 1) % 7
                    day += 1
            # к концу недели
            if i + 2 < N and words[i + 2].lower() == "недели" and words[i + 1].lower() == "концу":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                while (day_week != 0):
                    day_week = (day_week + 1) % 7
                    day += 1 
            # до следующей недели
            if i + 2 < N and words[i + 2].lower() == "недели" and words[i + 1].lower() == "следующей":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                while (day_week != 0):
                    day_week = (day_week + 1) % 7
                    day += 1
            # до конца недели
            if i + 2 < N and words[i + 2].lower() == "недели" and words[i + 1].lower() == "конца":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                while (day_week != 0):
                    day_week = (day_week + 1) % 7
                    day += 1             
            # к следующему месяцу
            if i + 2 < N and words[i + 2].lower() == "месяцу" and words[i + 1].lower() == "следующему":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                start_month = 1
            # к концу месяца
            if i + 2 < N and words[i + 2].lower() == "месяца" and words[i + 1].lower() == "концу":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                start_month = 1
            # до следующего месяца
            if i + 2 < N and words[i + 2].lower() == "месяца" and words[i + 1].lower() == "следующего":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                start_month = 1
            # до конца месяца
            if i + 2 < N and words[i + 2].lower() == "месяца" and words[i + 1].lower() == "конца":
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                start_month = 1
        if words[i].lower() == "через":
            # через 2 дня/недели/месяца/года
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "дня" or words[i + 2].lower() == "дней"):
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]
                day += int(words[i + 1])
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "недель" or words[i + 2].lower() == "недели"):
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]                
                day += int(words[i + 1]) * 7
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "месяца" or words[i + 2].lower() == "месяцев"):
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]                
                month += int(words[i + 1])
            if i + 2 < N and words[i + 1].isdigit() and (words[i + 2].lower() == "год" or words[i + 2].lower() == "лет"):
                stack += [i]
                stack += [i + 1]
                stack += [i + 2]                
                year += int(words[i + 1])
            # через день/неделю/месяц/год
            if i + 2 < N and words[i + 1].lower() == "день":
                stack += [i]
                stack += [i + 1]               
                day += 1
            if i + 2 < N and words[i + 1].lower() == "неделю":
                stack += [i]
                stack += [i + 1] 
                day += 7
            if i + 2 < N and words[i + 1].lower() == "месяц":
                stack += [i]
                stack += [i + 1] 
                month += 1
            if i + 2 < N and words[i + 1].lower() == "год":
                stack += [i]
                stack += [i + 1] 
                year += 1
    goal = []
    for i in range(len(words)):
        if i not in stack:
            goal += [words[i]]
            
    tod = datetime.datetime.now()
    d = datetime.timedelta(days = day) 
    a = tod + d
    goal[0] = goal[0][0].upper() + goal[0][1:]
    goal[-1] = goal[-1] + '.'
    if specific_date:
        print(a.year, month, day)
    if start_month:
        if month == 12:
            print("Дедлайн:", a.year + 1, 0, 0)
        else:
            print("Дедлайн:", a.year, a.month + 1, 0)
    else:   
        print("Дедлайн:", a.year + year + (a.month + month) // 12, (a.month + month) % 12, a.day)
    print("Цель:", ' '.join(goal), '\n')
           
goal_analysis("хочу научиться плавать через 40 дней")
goal_analysis("До вторника надо навестить бабушку")
goal_analysis("Через 10 дней необходимо сдать доклад")
goal_analysis("До завтра выброшу мусор")
goal_analysis("Хочу научиться говорить на испанском до конца недели")
goal_analysis("До 10 августа нужно выучить 10 слов")

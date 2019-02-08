import datetime
import pymorphy2
import calendar

end_start_next = ["следующий", "конец", "начало"]
measurement = ["день", "неделя", "месяц", "год"]

def different_forms(word, ):
    mas = [word, word, word, word]

def mypush(stack, i, c):
    for j in range(c):
        stack += [i + j]
    
def myreplace(word_next, word_next_next, word1, word2):
    return word_next_next == word1 and word_next == word2

def conditions(i, words, ind_meas):
    if i + 2 >= len(words):
        return False
    morph = pymorphy2.MorphAnalyzer()
    word_next_next = morph.parse(words[i + 2])[0].normal_form
    word_next = morph.parse(words[i + 1].lower())[0].normal_form
    b1 = word_next == end_start_next[0] and measurement[ind_meas] == word_next_next
    b2 = word_next == end_start_next[1] and measurement[ind_meas] == word_next_next
    return b1 or b2

def conditions_through(i, word1, word2, words):
    return i + 2 < len(words) and words[i + 1].isdigit() and (words[i + 2].lower() == word1 or words[i + 2].lower() == word2)
def conditions_through0(i, words, word1):
    return i + 2 < len(words) and words[i + 1].lower() == word1

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
    if new_words[-1][-1] == '.':
        new_words[-1] = new_words[-1][:-1]
    return new_words    

def goal_analysis(s):
    morph = pymorphy2.MorphAnalyzer()
    now = datetime.datetime.now()
    words = number_in_digits(s)
    N = len(words)
    # указываю текущее время
    month = 0
    day = 0
    year = 0
    day_week = datetime.datetime.today().weekday()
    start_month = 0
    start_year = 0
    start_day = 0
    specific_date = 0
    mas_month = ["январь", "февраль", "март", 
           "апрель", "май", "июнь",
           "июль", "август", "сентябрь",
           "октябрь", "ноябрь",	"декабрь"]
    mas_days = ["понедельнику", "вторнику", "среде", "четвергу", "пятнице", "субботе", "воскресенью"]
    mas_days2 = ["понедельника", "вторника", "среды", "четверга", "пятницы", "субботы", "воскресенья"]
    stack = []
    for i in range(N):
        if words[i].lower() == "завтра":
            day += 2
            stack += [i]
        if words[i].lower() == "послезавтра":
            day += 3
            stack += [i]
        if words[i].lower() == "к" or words[i].lower() == "ко"  or words[i].lower() == "до":
          
            if (i + 3 < N):
                w2 = morph.parse(words[i + 1])[0].normal_form
                w3 = morph.parse(words[i + 2])[0].normal_form
                w4 = morph.parse(words[i + 3])[0].normal_form
                if w2 == "конец" and w3 == "следующий":
                    if w4 == "день":
                        day = 2
                    if w4 == "неделя":
                        mypush(stack, i, 4)
                        while (day_week != 0):
                            day_week = (day_week + 1) % 14
                            day += 1 
                    if w4 == "месяц":
                        mypush(stack, i, 4)
                        start_month = 2                        
                    if w4 == "год":
                        mypush(stack, i, 4)
                        start_year = 2
                    
                if w2 == "начало" and w3 == "следующий":
                    if w4 == "день":
                        mypush(stack, i, 4)
                        day = 1
                    # к ... неделе
                    if w4 == "неделя":
                        mypush(stack, i, 4)
                        while (day_week != 0):
                            day_week = (day_week + 1) % 7
                            day += 1       
                    # к ... месяцу
                    if w4 == "месяц":
                        mypush(stack, i, 4)
                        start_month = 1
                    # к ... года
                    if w4 == "год":
                        mypush(stack, i, 4)
                        start_year = 1
                    
                     
            # до завтра
            if words[i + 1].lower() == "завтра":
                day += 1
                stack += [i]
                
            if words[i + 1].lower() == "завтрашнему":
                mypush3(stack, i)
                day += 1
                
            # к первому декабря
            if words[i + 1].isdigit() and morph.parse(words[i + 2])[0].normal_form in mas_month:
                mypush(stack, i, 3)
                day = int(words[i + 1])
                month = mas_month.index(morph.parse(words[i + 2])[0].normal_form) + 1
                specific_date = 1
            
            # к январю
            if morph.parse(words[i + 1])[0].normal_form in mas_month:
                mypush(stack, i, 2)
                day = 1
                month = mas_month.index(morph.parse(words[i + 1])[0].normal_form) + 1
                specific_date = 1
              
            # к понедельнику
            if words[i + 1].lower() in mas_days or words[i + 1].lower() in mas_days2:
                mypush(stack, i, 2)
                if words[i + 1].lower() in mas_days:
                    while (day_week != mas_days.index(words[i + 1])):
                        day_week = (day_week + 1) % 7
                        day += 1
                if words[i + 1].lower() in mas_days2:
                    while (day_week != mas_days2.index(words[i + 1])):
                        day_week = (day_week + 1) % 7
                        day += 1
            # к ... дня
            if conditions(i, words, 0):
                mypush(stack, i, 3)
                day = 1
            # к ... неделе
            if conditions(i, words, 1):
                mypush(stack, i, 3)
                while (day_week != 0):
                    day_week = (day_week + 1) % 7
                    day += 1       
            # к ... месяцу
            if conditions(i, words, 2):
                mypush(stack, i, 3)
                start_month = 1
            # к ... года
            if conditions(i, words, 3):
                mypush(stack, i, 3)
                start_year = 1
        if words[i].lower() == "через":
            # через 2 дня/недели/месяца/года
            if conditions_through(i, "дня", "дней", words):
                mypush(stack, i, 3)
                day += int(words[i + 1])
            if conditions_through(i, "недель", "недели", words):
                mypush(stack, i, 3)              
                day += int(words[i + 1]) * 7
            if conditions_through(i, "месяца", "месяцев", words):
                mypush(stack, i, 3)               
                month += int(words[i + 1])
            if conditions_through(i, "год", "лет", words):
                mypush(stack, i, 3)                
                year += int(words[i + 1])
            # через день/неделю/месяц/год
            if conditions_through0(i, words, "день"):
                mypush(stack, i, 2)             
                day += 1
            if conditions_through0(i, words, "неделю"):
                mypush(stack, i, 2)
                day += 7
            if conditions_through0(i, words, "месяц"):
                mypush(stack, i, 2)
                month += 1
            if conditions_through0(i, words, "год"):
                mypush(stack, i, 2)
                year += 1
    goal = []
    for i in range(len(words)):
        if i not in stack:
            goal += [words[i]]
            
    tod = datetime.datetime.now()
    d = datetime.timedelta(days = day) 
    a = tod + d
    goal[0] = goal[0][0].upper() + goal[0][1:]
    if goal[-1][-1] != '.':
        goal[-1] = goal[-1] + '.'
    if start_year:
        print(a.year + start_year, 1, 1)
    elif specific_date:
        if month < a.month or (a.month >= month and a.day < day):
            print(a.year + 1, month, day)
        else:
            print(a.year, month, day)
    elif start_month:
        if a.month + start_month >= 13:
            print("Дедлайн:", a.year + 1, (a.month + start_month) % 12, 1)
        else:
            print("Дедлайн:", a.year, a.month + start_month, 1)        
    else:   
        print("Дедлайн:", a.year + year + (a.month + month) // 12, (a.month + month) % 12, a.day)
    print("Цель:", ' '.join(goal), '\n')
           
s = input()         
           
goal_analysis(s)

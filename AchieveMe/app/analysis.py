import datetime
import pymorphy2
import calendar

end_start_next = ["следующий", "конец", "начало"]
measurement = ["день", "неделя", "месяц", "год"]
day_adj = ["одни", "двое", "трое", "четверо", "пятеро", "шестеро", "семеро"]

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
    b3 = word_next == end_start_next[2] and measurement[ind_meas] == word_next_next 
    return b1 or b2 or b3

def conditions_through0(i, words, word1):
    return i + 1 < len(words) and words[i + 1].lower() == word1

def number_in_digits(s):
    nums_letters = ['сорок', 'тридцать', 'семь', 'восемнадцать', 'пятьдесят', 'семьдесят', 'девяносто', 'десять', 'семнадцать', 'девятьсот', 'восемьдесят', 'шестьдесят', 'девятнадцать', 'девять', 'шестнадцать', 'двенадцать', 'двадцать', 'пятнадцать', 'пять', 'одиннадцать', 'пятьсот', 'восемь', 'тринадцать', 'семьсот', 'два', 'шесть', 'ноль', 'шестьсот', 'восемьсот', 'двести', 'четыреста', 'четыре', 'один', 'три', 'четырнадцать', 'триста', 'сто', 'тысяча', "одни", "двое", "трое", "четверо", "пятеро", "шестеро", "семеро"]
    nums_digits = [40, 30, 7, 18, 50, 70, 90, 10, 17, 90, 80, 60, 19, 9, 16, 12, 20, 15, 5, 11, 500, 8, 13, 700, 2, 6, 0, 600, 800, 200, 400, 4, 1, 3, 14, 300, 100, 1000, 1, 2, 3, 4, 5, 6, 7]
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
    if s == "":
        return "", now     
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
    mas_days = ["понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье"]
    stack = []
    for i in range(N):
        w0 = morph.parse(words[i].lower())[0].normal_form
        if i + 1 < N:
            w1 = morph.parse(words[i + 1].lower())[0].normal_form 
        if i + 2 < N:
            w2 = morph.parse(words[i + 2].lower())[0].normal_form
        if i + 3 < N:
            w3 = morph.parse(words[i + 3].lower())[0].normal_form
        if i + 4 < N:
            w4 = morph.parse(words[i + 4].lower())[0].normal_form  
        if '.' in w0:
            x1 = w0.split('.')[0]
            x2 = w0.split('.')[1]
            if x1.isdigit() and x2.isdigit():
                if i - 1 > 0 and words[i - 1] == 'к' or words[i - 1] == 'ко':
                    stack += [i - 1]
                stack += [i]
                if len(w0.split('.')) > 2:
                    x3 = w0.split('.')[2]
                    year = int(x3)
                day = int(x1)
                month = int(x2)
                specific_date = 1
            
        if w0 == "сегодня":
            stack += [i]
        if w0 == "завтра":
            day += 1
            stack += [i]
        if w0 == "послезавтра":
            day += 2
            stack += [i]
        if w0 == "к" or w0 == "до" or w0 == "в" or w0 == "по":
            if (i + 2 < N):
                if w1 == "конец":
                    if (i + 3 < N):
                        if w2 == "следующий":
                            if w3 == "день":
                                day = 2
                            if w3 == "неделя":
                                mypush(stack, i, 4)
                                while (day_week != 0):
                                    day_week = (day_week + 1) % 14
                                    day += 1 
                            if w3 == "месяц":
                                mypush(stack, i, 4)
                                start_month = 2                        
                            if w3 == "год":
                                mypush(stack, i, 4)
                                start_year = 2
                
                if w1 == "начало":
                    if (i + 3 < N):
                        if w2 == "следующий":
                            if w3 == "день":
                                mypush(stack, i, 4)
                                day = 1
                            # к ... неделе
                            if w3 == "неделя":
                                mypush(stack, i, 4)
                                while (day_week != 0):
                                    day_week = (day_week + 1) % 7
                                    day += 1       
                            # к ... месяцу
                            if w3 == "месяц":
                                mypush(stack, i, 4)
                                start_month = 1
                            # к ... года
                            if w3 == "год":
                                mypush(stack, i, 4)
                                start_year = 1
                    
                     
            # до завтра
            if w1 == "завтра":
                stack += [i]
                
            if w1 == "завтрашний":
                mypush(stack, i, 3)
                day += 1
            
            # к январю
            if w1 in mas_month:
                mypush(stack, i, 2)
                day = 1
                month = mas_month.index(w1) + 1
                specific_date = 1
                if i + 2 < N and w2.isdigit():
                    year = int(w2)
                    mypush(stack, i, 4)
                    
              
            # к понедельнику
            if w1 in mas_days:
                mypush(stack, i, 2)
                if w1 in mas_days:
                    while (day_week != mas_days.index(w1)):
                        day_week = (day_week + 1) % 7
                        day += 1
            # к ... дня
            if conditions(i, words, 0):
                mypush(stack, i, 3)
                day += 1
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
        if w0 == "через" or w0 == "и" :
            # через 2 дня/недели/месяца/года
            if i + 2 < N:
                w2 = morph.parse(words[i + 2])[0].normal_form
                if w2 == measurement[0] or w2 == "сутки":
                    mypush(stack, i, 3)
                    day += int(w1)
                if w2 == measurement[1]:
                    mypush(stack, i, 3)          
                    day += int(w1) * 7
                if w2 == measurement[2]:
                    mypush(stack, i, 3)              
                    month += int(w1)
                if w2 == measurement[3]:
                    mypush(stack, i, 3)           
                    year += int(w1)
            # через день/неделю/месяц/год
            if w1 == "день" or w1 == "сутки":
                mypush(stack, i, 2)             
                day += 1
            if w1 == "неделя":
                mypush(stack, i, 2)
                day += 7
            if w1 == "месяц":
                mypush(stack, i, 2)
                month += 1
            if w1 == "год":
                mypush(stack, i, 2)
                year += 1
            if w1 == "полтора":
                mypush(stack, i, 3)
                if w1 == "неделя":
                    day += 10
                if w1 == "месяц":
                    day += 45
                if w1 == "год":
                    day += 547
            if w1 == "полнеделя":
                mypush(stack, i, 2)
                day += 3
            if w1 == "полмесяца":
                mypush(stack, i, 2)
                day += 15
            if w1 == "полгода":
                mypush(stack, i, 2)
                day += 182
        if i + 1 < N and words[i].isdigit() and w1 in mas_month:
            if i - 1 > 0 and words[i - 1] == 'к' or words[i - 1] == 'ко':
                stack += [i - 1]
            mypush(stack, i, 2)
            day = int(words[i])
            month = mas_month.index(w1) + 1
            specific_date = 1
            if i + 3 < N and w2.isdigit():
                year = int(w2)
                mypush(stack, i, 4)
                    
            
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
        result = datetime.datetime(a.year + start_year, 1, 1)
    elif specific_date:
        if year:
            result = datetime.datetime(year, month, day)
        elif month < tod.month or (tod.month >= month and tod.day > day):
            result = datetime.datetime(tod.year + 1, month, day)
        else:
            result = datetime.datetime(tod.year, month, day)
    elif start_month:
        if a.month + start_month >= 13:
            result = datetime.datetime(a.year + 1, (a.month + start_month) % 12, 1)
        else:
            result = datetime.datetime(a.year, a.month + start_month, 1)
    else:   
        result = datetime.datetime(a.year + year + (a.month + month) // 12, (a.month + month) % 12, a.day)
    return ' '.join(goal), result

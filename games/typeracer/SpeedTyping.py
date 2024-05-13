import random
import time

# click is an external library used for getting a single character input at a time
import click
# playsound is a v small library soleley used for playing an audio


# Function to generate a random sentence from a file (text.txt)
def generate_random_sentence(mode):
    text_file = open("text.txt", "r")
    lines = text_file.readlines()
    if mode == '1':
        return random.choice(lines).replace("\n" , "") + random.choice(lines)
    return random.choice(lines)

# Function for printing the current string each time a character is entered
def printStr_Helper(st , start_time , sentence , mistakes , mode):
    click.clear()
    print()
    # A little custom progressbar that either works on time increment or progress progression
    if(mode == '1'):
        x= f'{"Time: "} [{"#" *round( round(time.time()-start_time))}🐸{"_" * round(  (60 - round(time.time()-start_time)))}]'
        click.echo(x.center(120))
    else:
        x = f'Progress:  [{"#" * round(len(st)/5)}🐸{"_" * round((len(sentence) - len(st)) / 5 )}]  Time: { round(time.time()-start_time)} '
        click.echo(x.center(120))
        
    print()
    # In case there are any mistakes indexes stored in the mistakes list (len not equal zero)
    if len(mistakes) != 0:
        start = 0
        for i in mistakes:
            # Loop will print upto the last mistake
            click.echo(click.style( sentence[start:i] ,bg='white' , fg='black'), nl=False)
            click.echo(click.style(sentence[i:i+1] , bg='red'), nl=False)
            start = i + 1

# This will print the remainaing "TYPED" sentence after the last mastake
            
        click.echo(click.style(sentence[mistakes[-1] + 1:len(st)] , bg='white' ,  fg='black'), nl=False)
        click.echo(click.style(sentence[len(st):] , bold= True) )

    else:
# In case no mistake was made
            # This will print the  "TYPED" sentence 
            click.echo(click.style(sentence[:len(st)],bg='white' , fg='black') , nl=False)
            #  This print the remaining sentece with no background color
            click.echo(click.style(sentence[len(st):] , bold= True) )

    print()
    click.echo("\rGo... : " + (st) , nl = False)
    
def getInput(sentence , mode):
    print("\r                           ",end="")
    click.echo("\rGo... : ", nl = False)
    finished = False
    mistakes = []
    corrects = 0
    # String initiation for storing characters in(st) after typing of each character of sentence 
    st = ""
    # Word count showing current iteration of word
    word = 0
    time_started = False
    # Fake time start to initialize variable and pass first loop iteration
    start_time = time.time()
    # While loop for as long as length of user typed string is less the string to be typed ( Replace is used for removing red colours from string)

    convo_start_time = time.time()
    while len(sentence.replace("\n" , "")) > (len(st)):
        if finished:  break
        if((mode == "1" and time.time() - start_time >= 60) or (finished)): 
            text_file = open("convo.txt", "w")
            if(mode == '1'):
                # x= f'{"Time= "} [{"#" *round( round(time.time()-start_time))}||{"_" * round(  (60 - round(time.time()-start_time)))}]\n'
                elapsed_time = time.time() - start_time
                percentage = int((elapsed_time / 60) * 100) # Percentage of 1 minute passed
                x = f'progress={percentage}\n'
            else:
                # x = f'Progress=  [{"#" * round(len(st)/5)}||{"_" * round((len(sentence) - len(st)) / 5 )}]  Time: { round(time.time()-start_time)}\n'
                progress = int((len(st) / len(sentence)) * 100) # Percentage of sentence typed
                elapsed_time = int(time.time() - start_time)
                x = f'progress={progress}\n'

            wpm = calculate_wpm(time.time() - start_time , st)
            y = f"wpm={int(wpm)}"
            text_file.writelines([x , y , "stop"])
            text_file.close()
            break
        # Get char gets a character as input
        c = click.getchar()
        if(not time_started):
            start_time = time.time()
            time_started = True
        
        # "\x08" is the code for backspace key
        if(c == "\x08" and word != 0):
            # Dont include the last character
            st = st[:len(st) - 1]
            if (word - 1) in mistakes:
                mistakes.remove(word - 1)
            else:
                corrects = corrects - 1

            word = word - 1
            click.clear()
            printStr_Helper(st  , start_time , sentence , mistakes , mode)


        # Enter key produced "\r" code
        elif(c == "\r"):
        # elif(c == "\r" and corrects > 60 and mode == "2"):
            finished = True

# repr() changes a character to raw form like for (a) it will be ('a') and for any special keys it will be a code with len > 3
#  hence only alphabets will pass
        if(len(repr(c)) == 3):
            if(c != sentence[word]):
                if len(st) != 0 and st[-1] != sentence[word -1]:    
                    # playsound("./error.mp3",False)
                    continue
                st = st +  c 
                mistakes.append(word)
            else:
                st = st + c
                corrects = corrects + 1
            printStr_Helper(st  , start_time , sentence , mistakes , mode)

            word = word + 1


        if(time.time() - convo_start_time >= 5):
            text_file = open("convo.txt", "w")
            if(mode == '1'):
                # x= f'{"Time= "} [{"#" *round( round(time.time()-start_time))}||{"_" * round(  (60 - round(time.time()-start_time)))}]\n'
                elapsed_time = time.time() - start_time
                percentage = int((elapsed_time / 60) * 100) # Percentage of 1 minute passed
                x = f'progress={percentage}\n'
            else:
                # x = f'Progress=  [{"#" * round(len(st)/5)}||{"_" * round((len(sentence) - len(st)) / 5 )}]  Time: { round(time.time()-start_time)}\n'
                progress = int((len(st) / len(sentence)) * 100) # Percentage of sentence typed
                elapsed_time = int(time.time() - start_time)
                x = f'progress={progress}\n'

            wpm = calculate_wpm(time.time() - start_time , st)
            y = f"wpm={int(wpm)}"
            text_file.writelines([x , y])
            text_file.close()
            convo_start_time = time.time()


    return st , corrects ,  round(time.time() - start_time )

# Function to update leader if user choses to
def update_leaderboard(w_p_min , accuracy):
    # Function will check if the current variable score is higher score than current iteration score  if so then insert the current user
    # at that point

    changedUserId = False
    text_file = open("leaderboard.txt", "r")
    lines = text_file.readlines()
    userScore = (w_p_min + accuracy)

    name = input("Enter your username  (Max 16 characters): ")
    while len(name) > 16 and len(name) == 0: name = input("Enter your username  (Max 16 characters): ")
# In case of an empty file
    if(len(lines) == 0):
        text_file.close()
        lines = [f"{name},{round(w_p_min)},{round(userScore)}"]
        text_file = open("leaderboard.txt", "w")
        text_file.writelines(lines)
        text_file.close()
        return lines,0
    
# If last person has better score then dont execute loop
    minScore = int(lines[-1].split(",")[-1])
    if (minScore) >= userScore :
        print("Last score")
        text_file.close()
        lines.append( f"\n{name},{round(w_p_min)},{round(userScore)}")
        print(lines)
        text_file = open("leaderboard.txt", "w")
        text_file.writelines(lines)
        text_file.close()
        return lines,len(lines) - 1
# In case the user lies in between
    for index,el in enumerate(lines):
        current_i_score = int(el.split(",")[-1])
        if(userScore > current_i_score ):
            changedUserId = index
            lines.insert(index , f"{name},{round(w_p_min)},{round(userScore)}\n")
            break
    text_file.close()
    text_file = open("leaderboard.txt", "w")
    print("lines" , lines)
    text_file.writelines(lines)
    text_file.close()
    return lines , changedUserId

# Function to calculate typing speed in WPM
def calculate_wpm(time_taken, sentence):
    sentence_length = len(sentence)
    time_in_mins = time_taken / 60
    words = sentence_length / 5
    return words / time_in_mins

# Function to print leaderboard in a tabular form
def print_leaderboard(leaderboard,userI):
    lines = leaderboard
    print(f'{"Position": <10}|    {"Name": <20}    |    {"Words Per Minute": <20}    |    {"Accuracy": <20}')
    print(f'--------------------------------------------------------------------------------------------')
    for index,el in enumerate(lines):
        el = el.replace("\n","")
        if el == "": continue
        if(userI == index):
            s = f'    {index + 1: 4d}  |    {el.split(",")[0]: <20}    |     {el.split(",")[1]: <20}    |     {f"{(int(el.split(",")[2])-int(el.split(",")[1]))}": <20} '
            click.echo(click.style(s , fg="green"))
            print("-" * ( len(s) - 10))
            continue
        s = f'    {index + 1: 4d}  |    {el.split(",")[0]: <20}    |     {(el.split(",")[1]): <20}    |     {f"{(int(el.split(",")[2])-int(el.split(",")[1]))}": <20} '
        print(s)
        print("-" * ( len(s) - 10))

# Main Function Of The Program
def begin():
    click.echo("Select mode: \n 1. One minute Rush \n 2. Speed Test \n Choice[1/2]: ",nl=False)
    mode = click.getchar(echo=True)
    while mode != "1" and mode != "2":
        print("\r \n Choice[1/2]: ",end="")
        mode = click.getchar(echo=True)
    click.clear()
    sentence = generate_random_sentence(mode)
    click.echo("Begin typing when you are ready ".center(120))
    print()
    click.echo( click.style(sentence , bold= True) )
    print()
    input_text , correctCount , time_taken = getInput(sentence  , mode)
    click.clear()
    wpm = calculate_wpm(time_taken, input_text)
    click.echo(f"\nYou took {round(time_taken)} seconds to type the sentence, achieving a speed of " + click.style(round(wpm) , fg='green') + " WPM.")
    # Formula for accuracy
    accuracy = round(((correctCount)/len(input_text)) * 100)
    print(f"Your accuracy is " + click.style(f"{accuracy} %", fg='green'))
    lb = ""
    # show leaderboard only in case accuracy was higher than 60 percenst
    if(accuracy > 60):
        click.echo("Showoff your skill in the offlien leaderboard? \n (1 for Yes) \n (2 for No) :" , nl=False)
        while(lb != "1" and lb != "2"):
            print("\r \n Choice[1/2]: ",end="")
            lb = click.getchar(echo=True)
        print()
    if(lb  != "2"):
        updated , userI = update_leaderboard(wpm , round(accuracy))
        print_leaderboard(updated , userI )

    click.echo("Try again ??[y/] : " , nl=False)
    take_again = click.getchar(echo=True)
    if(take_again == "y" ): 
        click.clear()
        begin()
    else:
        print("\nSayonara!")

print(" " * 10 , "________                 _________   ________           _____ ")
print(" " * 10 , "__  ___/_______________________  /   ___  __/_____________  /_")
print(" " * 10 , "_____ \\___  __ \\  _ \\  _ \\  __  /    __  /  _  _ \\_  ___/  __/")
print(" " * 10 , "____/ /__  /_/ /  __/  __/ /_/ /     _  /   /  __/(__  )/ /_  ")
print(" " * 10 , "/____/ _  .___/\\___/\\___/\\__,_/      /_/    \\___//____/ \\__/  ")
print(" " * 10 , "        /_/                                                   ")
# Clear convo.txt file 
text_file = open("convo.txt", "w")
text_file.write("")
text_file.close()

begin()


import random
import time

# click is an external library used for getting a single character input at a time
import click
# playsound is a v small library soleley used for playing an audio
import playsound

# Function to generate a random sentence from a file (text.txt)
def generate_random_sentence(mode):
    """    Generate a random sentence from a file.

    This function reads the content of the file "text.txt" and generates a
    random sentence based on the specified mode.

    Args:
        mode (str): The mode for generating the random sentence. If '1', two random lines
            are concatenated to form the sentence.

    Returns:
        str: A randomly generated sentence based on the specified mode.
    """

    text_file = open("text.txt", "r")
    lines = text_file.readlines()
    if mode == '1':
        return random.choice(lines).replace("\n" , "") + random.choice(lines)
    return random.choice(lines)

# Function for printing the current string each time a character is entered
def printStr_Helper(st , start_time , sentence , mistakes , mode):
    """    Print the current string each time a character is entered.

    This function prints the current string each time a character is
    entered, along with a custom progress bar. It also highlights any
    mistakes made in the input string.

    Args:
        st (str): The current string being typed.
        start_time (float): The start time of typing.
        sentence (str): The complete sentence to be typed.
        mistakes (list): A list of indexes where mistakes were made in the input string.
        mode (str): The mode of progress bar, either '1' for time increment or any other
            value for progress progression.
    """

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
    """    Get input from the user and calculate typing speed and accuracy.

    This function takes a sentence and a mode as input and allows the user
    to type the given sentence. It calculates the typing speed, accuracy,
    and progress and writes the results to a file.

    Args:
        sentence (str): The sentence that the user needs to type.
        mode (str): The mode of typing, either "1" for timed mode or "2" for free mode.

    Returns:
        tuple: A tuple containing: - str: The string typed by the user. - int: The
            number of correct characters typed. - int: The time taken to type the
            sentence.
    """

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
        # if finished:  break
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
                if progress > 100:
                    progress = 100
                x = f'progress={progress}\n'

            wpm = calculate_wpm(time.time() - start_time , st)
            y = f"wpm={int(wpm)}\n"
            z = f"accuracy={round(((corrects)/len(st)) * 100)}"
            text_file.writelines([x , y , z ])
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
                    playsound.playsound("./error.mp3",False)
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
            y = f"wpm={int(wpm)}\n"
            z = f"accuracy={round(((corrects)/len(st)) * 100)}"
            text_file.writelines([x , y , z])
            text_file.close()
            convo_start_time = time.time()


    return st , corrects ,  round(time.time() - start_time )

# Function to calculate typing speed in WPM
def calculate_wpm(time_taken, sentence):
    """    Calculate the typing speed in words per minute (WPM).

    It calculates the typing speed by dividing the total number of words in
    the given sentence by the time taken to type the sentence in minutes.

    Args:
        time_taken (float): The time taken to type the sentence in seconds.
        sentence (str): The input sentence for which typing speed is to be calculated.

    Returns:
        float: The typing speed in words per minute (WPM).
    """

    sentence_length = len(sentence)
    time_in_mins = time_taken / 60
    words = sentence_length / 5
    return words / time_in_mins


# Main Function Of The Program
def begin():
    """    Main function to start the typing speed test game.

    Selects the mode for the game, generates a random sentence based on the
    mode, prompts the user to type the sentence, calculates the typing speed
    and accuracy, and optionally updates the leaderboard.
    """

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
        click.echo("Showoff your skill in the server leaderboard? \n (1 for Yes) \n (2 for No) :" , nl=False)
        while(lb != "1" and lb != "2"):
            print("\r \n Choice[1/2]: ",end="")
            lb = click.getchar(echo=True)
        print()

    text_file = open("convo.txt", "a")
    if(lb  != "2"):
        text_file.writelines(["\nleaderboard=true","\nstop"])

        # updated , userI = update_leaderboard(wpm , round(accuracy))
        # print_leaderboard(updated , userI )
    else:
        text_file.writelines(["\nleaderboard=false","\nstop"])
    
    text_file.close()


    print("\nSayonara!")

print(" " * 10 , "________                 _________   ________           _____ ")
print(" " * 10 , "__  ___/_______________________  /   ___  __/_____________  /_")
print(" " * 10 , "_____ \\___  __ \\  _ \\  _ \\  __  /    __  /  _  _ \\_  ___/  __/")
print(" " * 10 , "____/ /__  /_/ /  __/  __/ /_/ /     _  /   /  __/(__  )/ /_  ")
print(" " * 10 , "/____/ _  .___/\\___/\\___/\\__,_/      /_/    \\___//____/ \\__/  ")
print(" " * 10 , "        /_/                                                   ")


begin()


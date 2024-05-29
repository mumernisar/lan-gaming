
import pip
# Clear convo.txt file 
text_file = open("convo.txt", "w")
text_file.write("")
text_file.close()

def import_or_install(package):
    try:
        __import__(package)
    except ImportError:
        pip.main(['install', '--user', package])

import_or_install("click")
import_or_install("playsound==1.2.2")
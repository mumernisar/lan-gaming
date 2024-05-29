
import pip
# Clear convo.txt file 
text_file = open("convo.txt", "w")
text_file.write("")
text_file.close()

def import_or_install(package):
    """    Import a package, and if it is not found, attempt to install it using
    pip.

    Args:
        package (str): The name of the package to import or install.

    Raises:
        ImportError: If the package cannot be imported.
    """

    try:
        __import__(package)
    except ImportError:
        pip.main(['install', '--user', package])

import_or_install("click")
import_or_install("playsound==1.2.2")
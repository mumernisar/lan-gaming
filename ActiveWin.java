import java.awt.*;

public class ActiveWin {
    public  static  void main(String[] args) {
        // Get the currently active window
        Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();

        
        // Check if the application running this code is active
        boolean isApplicationActive = activeWindow instanceof Frame || activeWindow instanceof Dialog;
        
        // Print the result
        if (activeWindow != null) {
            System.out.println("Active Window: " + activeWindow);
        } else {
            System.out.println("No active window found.");
        }
        
        System.out.println("Is Application Active: " + isApplicationActive);

    }
    public  void check() {
        Window w = getSelectedWindow(Window.getWindows());
        System.out.println("Selected Window: " + w);
    }
    Window getSelectedWindow(Window[] windows) {
        Window result = null;
        for (int i = 0; i < windows.length; i++) {
            Window window = windows[i];
            if (window.isActive()) {
                result = window;
            } else {
                Window[] ownedWindows = window.getOwnedWindows();
                if (ownedWindows != null) {
                    result = getSelectedWindow(ownedWindows);
                }
            }
        }
        return result;
    } 
}


// import java.awt.*;

// public class ActiveWin {
//     public static void main(String[] args) {
//         // Get the currently active window
//         Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        
//         // Check if the application running this code is active
//         boolean isApplicationActive = activeWindow instanceof Frame || activeWindow instanceof Dialog;
        
//         // Print the result
//         System.out.println("Is application active: " + activeWindow + isApplicationActive);
//     }
// }

// // import javax.swing.*;
// // import java.awt.*;
// // import java.awt.event.*;

// // public class ActiveWin extends JFrame {
// //     public ActiveWin() {
// //         // Create a timer that checks if the window is active every second
// //         Timer timer = new Timer(1000, new ActionListener() {
// //             public void actionPerformed(ActionEvent e) {
// //                 // Get the currently active window
// //                 Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();

// //                 // Check if the application running this code is active
// //                 boolean isApplicationActive = activeWindow instanceof Frame || activeWindow instanceof Dialog;

// //                 // Print the result
// //                 if (activeWindow != null) {
// //                     System.out.println("Active Window: " + activeWindow);
// //                 } else {
// //                     System.out.println("No active window found.");
// //                 }

// //                 System.out.println("Is Application Active: " + isApplicationActive);
// //             }
// //         });

// //         // Start the timer
// //         timer.start();

// //         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// //         this.setSize(300, 200);
// //         this.setVisible(true);
// //     }

// //     public static void main(String[] args) {
// //         new ActiveWin();
// //     }
// // }
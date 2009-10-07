package org.sakaiquebec.opensyllabus.client.ui.util;

/** 
 * <pre> 
 * Generic printing class 
 * can be used to print the Window it self, DOM.Elements, UIObjects (Widgets) and plain HTML 
 * package br.com.freller.tool.client; 
 * Usage: 
 *      You must insert this iframe in your host page: 
 *              <iframe id="__printingFrame" style="width:0;height:0;border:0"></iframe> 
 * 
 *      Window: 
 *              Print.it(); 
 * 
 *      Objects/HTML: 
 *              Print.it(RootPanel.get("myId")); 
 *              Print.it(DOM.getElementById("myId")); 
 *              Print.it("Just <b>Print.it()</b>!"); 
 * 
 *      Objects/HTML using styles: 
 *      
 *      Warning: You can't use \" in your style String   
 *      
 *              Print.it("<link rel=StyleSheet type=text/css media=paper href=/paperStyle.css />", RootPanel.get('myId')); 
 *              Print.it("<style type=text/css media=paper > .newPage { page-break-after: always; } </style>", "Hi<p class=newPage></p>By"); 
 * 
 * </pre> 
 */ 

import com.google.gwt.user.client.Command; 
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand; 
import com.google.gwt.user.client.Element; 
import com.google.gwt.user.client.Window; 
import com.google.gwt.user.client.ui.UIObject; 
import com.google.gwt.user.client.Timer;

public class Print { 
    
    /**                                                                                                                                            
     * If true, use a Timer instead of DeferredCommand to print the internal fram                                                                  
     */
    public static boolean USE_TIMER  = false;

    /**                                                                                                                                            
     * Time in seconds to wait before printing the internal frame when using Timer                                                                 
     */
    public static int TIMER_DELAY = 2;

    public static native void it() /*-{ 
        $wnd.print();
        $wnd.alert("I am printing a page.");  
    }-*/; 

    public static native void buildFrame(String html) /*-{ 
    	// $wnd.alert(html);
        var frame = $doc.getElementById('__printingFrame'); 
        if (!frame) { 
            $wnd.alert("Error: Can't find printing frame."); 
            return; 
        } 
        var doc = frame.contentWindow.document; 
        doc.open(); 
        doc.write(html); 
        doc.close(); 
        // $wnd.alert("I am building a printing frame."); 
    }-*/; 
    
    public static native void printFrame() /*-{ 
        var frame = $doc.getElementById('__printingFrame'); 
        frame = frame.contentWindow; 
        frame.focus(); 
        frame.print(); 
       // $wnd.alert("I am printing the frame."); 
    }-*/; 

    public static Command printFrameCommmand    = new Command() {
        public void execute() {
            printFrame();
        }
    };
     
    public static void it(String html) {
        try {
            buildFrame(html);

            if (USE_TIMER)
                new PrintTimer();
            else
                DeferredCommand.addCommand(printFrameCommmand);

        } catch (Throwable exc) {
            Window.alert(exc.getMessage());
        }
    }
    
    public static class PrintTimer extends Timer {

        public PrintTimer() {
            schedule(TIMER_DELAY * 1000);
        }

        public void run() {
            printFrame();
        }
    }
    
     public static void it(UIObject obj) {
	 it("", obj);
     }
     
     public static void it(Element element) {
	 it("", element);
    }
    
     public static void it(String style, String it) {
         it("<html>"
            +"<head>"
            +"<meta http-equiv=\"Content-Type\"          content=\"text/html; charset=utf-8\">"
            +"<meta http-equiv=\"Content-Style-Type\"    content=\"text/css\">"
            +    style
            +"</head>"+"<body>"
            +    it
            +"</body>"+
            "</html>");
     }
    
    public static void it(String style, UIObject obj) {
       it(style, obj.getElement());
    }
    
    public static void it(String style, Element element) {
        it(style, DOM.toString(element));
    }
}
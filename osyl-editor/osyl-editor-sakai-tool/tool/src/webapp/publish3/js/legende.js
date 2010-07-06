var nav = (document.layers); 

var iex = (document.all);

var skn = (nav) ? document.topdeck : topdeck.style;

if (nav) document.captureEvents(Event.MOUSEMOVE);

document.onmousemove = get_mouse;



function pop(pic1,pic2,pic3,pic4,msg1,msg2,msg3,msg4,bak) {

var content ="<TABLE WIDTH=215 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR=#CCCCFF><TR><TD><TABLE WIDTH=100% BORDER=0 CELLPADDING=0 CELLSPACING=0><TR><TD><CENTER><FONT face=Calibri COLOR=#000066 SIZE=2><B>Légende des ressources de ZoneCours</B></FONT></CENTER></TD></TR></TABLE><TABLE WIDTH=100% BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="+bak+"><TR><TD><FONT COLOR=#000000 SIZE=2><img src="+pic1+" align=left border=0>"+msg1+"</FONT></TD></TR><TR><TD><FONT COLOR=#000000 SIZE=2><img src="+pic2+" align=left border=0>"+msg2+"</FONT></TD></TR><TR><TD><FONT COLOR=#000000 SIZE=2><img src="+pic3+" align=left border=0>"+msg3+"</FONT></TD></TR><TR><TD><FONT COLOR=#000000 SIZE=2><img src="+pic4+" align=left border=0>"+msg4+"</FONT></TD></TR></TABLE></TD></TR></TABLE>";

if (nav) { 

skn.document.write(content); 

skn.document.close();

skn.visibility = "visible";

}

else if (iex) 

{

document.all("topdeck").innerHTML = content;

skn.visibility = "visible"; 

}

}



function get_mouse(e) 

{

var x = (nav) ? e.pageX : event.x+document.body.scrollLeft; 

var y = (nav) ? e.pageY : event.y+document.body.scrollTop;

skn.left = x - 60;

skn.top = y+20;

}



function kill() 

{

skn.visibility = "hidden";

}
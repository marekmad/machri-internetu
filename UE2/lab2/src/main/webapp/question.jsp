<?xml version="1.0" encoding="UTF-8"?>

<%@page import="java.util.List"%>
<%@page import="at.ac.tuwien.big.we14.lab2.api.Choice"%>
<jsp:useBean id="game" scope="session" class="at.ac.tuwien.big.we14.lab2.api.impl.Game" />


<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Business Informatics Group Quiz</title>
        <link rel="stylesheet" type="text/css" href="style/screen.css" />
        <script src="js/jquery.js" type="text/javascript"></script>
        <script src="js/framework.js" type="text/javascript"></script>
    </head>
    <body id="questionpage" onload="loadLastTime()">
        <a class="accessibility" href="BigQuizServlet?action=question">Zur Frage springen</a>
        <header role="banner" aria-labelledby="mainheading"><h1 id="mainheading"><span class="accessibility">Business Informatics Group</span> Quiz</h1> <input id="questionid" type = "hidden" value=<%=game.getAcctuallQuestion().getId()%> accesskey="s"/></header>
        <nav role="navigation" aria-labelledby="navheading">
            <h2 id="navheading" class="accessibility">Navigation</h2>
            <ul>
                <li><a id="logoutlink" title="Klicke hier um dich abzumelden" href="#" accesskey="l">Abmelden</a></li>
            </ul>
        </nav>
        
        <!-- round info -->
        <section role="main">
            <section id="roundinfo" aria-labelledby="roundinfoheading">
                <h2 id="roundinfoheading" class="accessibility">Spielerinformationen</h2>
                <div id="player1info">
                    <span id="player1name">Spieler 1</span>
                    <ul class="playerroundsummary">
                        <li><span class="accessibility">Frage 1:</span><span id="player1answer0" class=<%=game.getPlayer1().getQuestionState(0) %>> <%=game.getPlayer1().getQuestionState(0) %></span></li>
                        <li><span class="accessibility">Frage 2:</span><span id="player1answer1" class=<%=game.getPlayer1().getQuestionState(1) %>> <%=game.getPlayer1().getQuestionState(1) %></span></li>
                        <li><span class="accessibility">Frage 3:</span><span id="player1answer2" class=<%=game.getPlayer1().getQuestionState(2) %>> <%=game.getPlayer1().getQuestionState(2) %></span></li>

                    </ul>
                </div>
                <div id="player2info">
                    <span id="player2name">Spieler 2</span>
                    <ul class="playerroundsummary">
                        <li><span class="accessibility">Frage 1:</span><span id="player2answer0" class=<%=game.getPlayer2().getQuestionState(0) %>><%=game.getPlayer2().getQuestionState(0) %></span></li>
                        <li><span class="accessibility">Frage 2:</span><span id="player2answer1" class=<%=game.getPlayer2().getQuestionState(1) %>><%=game.getPlayer2().getQuestionState(1) %></span></li>
                        <li><span class="accessibility">Frage 3:</span><span id="player2answer2" class=<%=game.getPlayer2().getQuestionState(2) %>><%=game.getPlayer2().getQuestionState(2) %></span></li>

                    </ul>
                </div>
                <div id="currentcategory"><span class="accessibility">Kategorie:</span> <%=game.getAcctuallQuestion().getCategory().getName() %></div>
            </section>
            
            <!-- Question -->
            <section id="question" aria-labelledby="questionheading">
                
                <form id="questionform" action="BigQuizServlet?action=submitAnswer" method="post">
                    <h2 id="questionheading" class="accessibility">Frage</h2>
                    <p id="questiontext"><%=game.getAcctuallQuestion().getText()%></p>
                    <ul id="answers">
                    	<%List<Choice> choices = game.getAcctuallQuestion().getAllChoices();%>
                   		<% for (int i = 0; i < 4; i ++) { %> 
                   				<li><input id=<%=choices.get(i).getId()%> type="checkbox" name = "opts" value = "<%=choices.get(i).getId()%>"/><label for=<%=choices.get(i).getId()%>><%= choices.get(i).getText() %> </label></li>
                				<!-- neda sa kliknut <li><input id="labeloption<%=String.valueOf(i) + "\""%> type="checkbox" name = "opts" value = "<%=choices.get(i).getId()%>"/><label for=<%="\"option"+String.valueOf(i)+"\"" %>><%= choices.get(i).getText() %> </label></li>-->
                		<% } %>
                    </ul>
                    <input id="timeleftvalue" type="hidden" value="100"/>
                    <input id="next" type="submit" value="weiter" accesskey="s"/>
                </form>
            </section>
            
            <section id="timer" aria-labelledby="timerheading">
                <h2 id="timerheading" class="accessibility">Timer</h2>
                <p><span id="timeleftlabel">Verbleibende Zeit:</span> <time id="timeleft"><%=game.getAcctuallQuestion().getFormatedMaxTime()%></time></p>
                <meter id="timermeter" min="0" low="20" value="100" max="100"/>
            </section>
            
            <section id="lastgame">
                <p>Letztes Spiel: <label id = "lastTime"></label></p>
            </section>
        </section>

        <!-- footer -->
        <footer role="contentinfo">© 2014 BIG Quiz</footer>
        
        <script type="text/javascript">
            //<![CDATA[
            
            // initialize time
            $(document).ready(function() {
                var maxtime = <%=game.getAcctuallQuestion().getMaxTime()%>;
                var hiddenInput = $("#timeleftvalue");
                var meter = $("#timer meter");
                var timeleft = $("#timeleft");
                
                hiddenInput.val(maxtime);
                meter.val(maxtime);
                meter.attr('max', maxtime);
                meter.attr('low', maxtime/100*20);
                timeleft.text(secToMMSS(maxtime));
            });
            
            function loadLastTime() {
    			if (typeof (Storage) !== "undefined") {

    				var storedValue = localStorage.getItem('time');
    				if (storedValue )
    					document.getElementById('lastTime').innerHTML = storedValue;
    				else {
    					document.getElementById('lastTime').innerHTML = "NEIN";
    					var lastT = new Date();
    					localStorage.setItem('time', lastT);
    				}

    				
    			} else {
    				document.getElementById('lastTime').innerHTML = "Browser does not support localStorage";
    			}
    		}
            
            // update time
            function timeStep() {
                var hiddenInput = $("#timeleftvalue");
                var meter = $("#timer meter");
                var timeleft = $("#timeleft");
                
                var value = $("#timeleftvalue").val();
                if(value > 0) {
                    value = value - 1;   
                }
                
                hiddenInput.val(value);
                meter.val(value);
                timeleft.text(secToMMSS(value));
                
                if(value <= 0) {
                    $('#questionform').submit();
                }
            }
            
            window.setInterval(timeStep, 1000);
            
            //]]>
        </script>
    </body>
</html>

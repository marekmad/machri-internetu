
package views.html.quiz

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.i18n._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._
import views.html._
import scala.collection._
/**/
object quizover extends BaseScalaTemplate[play.api.templates.HtmlFormat.Appendable,Format[play.api.templates.HtmlFormat.Appendable]](play.api.templates.HtmlFormat) with play.api.templates.Template1[QuizGame,play.api.templates.HtmlFormat.Appendable] {

    /**/
    def apply/*1.2*/(game: QuizGame):play.api.templates.HtmlFormat.Appendable = {
        _display_ {
def /*2.2*/player1/*2.9*/ = {{ game.getPlayers().get(0) }};def /*3.2*/player2/*3.9*/ = {{ game.getPlayers().get(1) }};def /*4.2*/message/*4.9*/ = {{ game.getMessage() }};def /*5.2*/winnerMessage/*5.15*/ = {{
    game.getWinner() match {
        case null => Messages("quiz.tie")
        case winner: QuizUser => Messages("quiz.winner", winner.getName())
    }
}};
Seq[Any](format.raw/*1.18*/("""
"""),format.raw/*2.41*/("""
"""),format.raw/*3.41*/("""
"""),format.raw/*4.34*/("""
"""),format.raw/*10.2*/("""
"""),_display_(Seq[Any](/*11.2*/main("main.quiz",
      pageid = "winnerpage",
      navigation = immutable.Map(routes.Authentication.logout.url -> "login.logout"))/*13.86*/ {_display_(Seq[Any](format.raw/*13.88*/("""
	<section role="main">
		<!-- winner message -->
            <section id="roundwinner" aria-labelledby="roundwinnerheading">
                <h2 id="roundwinnerheading" class="accessibility">"""),_display_(Seq[Any](/*17.68*/Messages("quiz.result"))),format.raw/*17.91*/("""</h2>
                <p class="roundwinnermessage">"""),_display_(Seq[Any](/*18.48*/winnerMessage)),format.raw/*18.61*/("""</p>
            </section>
        
            <!-- round info -->    
            <section id="roundinfo" aria-labelledby="roundinfoheading">
                <h2 id="roundinfoheading" class="accessibility">"""),_display_(Seq[Any](/*23.66*/Messages("quiz.gameinfo"))),format.raw/*23.91*/("""</h2>
                <div id="player1info" class="playerinfo">
                    <span id="player1name" class="playername">"""),_display_(Seq[Any](/*25.64*/player1/*25.71*/.getName())),format.raw/*25.81*/("""</span>
                    <p id="player1roundcounter" class="playerroundcounter">"""),_display_(Seq[Any](/*26.77*/Messages("quiz.wonrounds"))),format.raw/*26.103*/(""": <span id="player1wonrounds" class="playerwonrounds">"""),_display_(Seq[Any](/*26.158*/game/*26.162*/.getWonRounds(player1))),format.raw/*26.184*/("""</span></p>
                </div>
                <div id="player2info" class="playerinfo">
                    <span id="player2name" class="playername">"""),_display_(Seq[Any](/*29.64*/player2/*29.71*/.getName())),format.raw/*29.81*/("""</span>
                    <p id="player2roundcounter" class="playerroundcounter">"""),_display_(Seq[Any](/*30.77*/Messages("quiz.wonrounds"))),format.raw/*30.103*/(""": <span id="player2wonrounds" class="playerwonrounds">"""),_display_(Seq[Any](/*30.158*/game/*30.162*/.getWonRounds(player2))),format.raw/*30.184*/("""</span></p>
                </div>
                """),_display_(Seq[Any](/*32.18*/helper/*32.24*/.form(routes.Quiz.newGame)/*32.50*/ {_display_(Seq[Any](format.raw/*32.52*/("""
                    <input id="next" type="submit" value=""""),_display_(Seq[Any](/*33.60*/Messages("quiz.start"))),format.raw/*33.82*/(""""/>
                """)))})),format.raw/*34.18*/("""
                
                
        	<section id="roundwinner" aria-labelledby="roundwinnerheading">
               
                <p class="myclass">"""),_display_(Seq[Any](/*39.37*/message)),format.raw/*39.44*/("""</p>
            </section>
            
            
            </section>
            <script type="text/javascript">
            //<![CDATA[
	            $(document).ready(function()"""),format.raw/*46.42*/("""{"""),format.raw/*46.43*/("""
	         	   if(supportsLocalStorage())"""),format.raw/*47.41*/("""{"""),format.raw/*47.42*/("""
	         		   localStorage["lastGame"] = new Date().getTime();
	         	   """),format.raw/*49.15*/("""}"""),format.raw/*49.16*/("""
	            """),format.raw/*50.14*/("""}"""),format.raw/*50.15*/(""");
            //]]>
            </script>
    </section>
""")))})))}
    }
    
    def render(game:QuizGame): play.api.templates.HtmlFormat.Appendable = apply(game)
    
    def f:((QuizGame) => play.api.templates.HtmlFormat.Appendable) = (game) => apply(game)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Sun Jun 01 21:29:32 CEST 2014
                    SOURCE: C:/data/skola_TU_WIEN/2014SS/Webengeneering/Ubung/UE4/lab4/app/views/quiz/quizover.scala.html
                    HASH: 56dde109ddd4cda658a140dfa0080292b33aac03
                    MATRIX: 810->1|903->20|917->27|962->62|976->69|1021->104|1035->111|1073->139|1094->152|1288->17|1317->59|1346->101|1375->136|1404->316|1442->319|1585->453|1625->455|1858->652|1903->675|1993->729|2028->742|2279->957|2326->982|2491->1111|2507->1118|2539->1128|2660->1213|2709->1239|2801->1294|2815->1298|2860->1320|3055->1479|3071->1486|3103->1496|3224->1581|3273->1607|3365->1662|3379->1666|3424->1688|3514->1742|3529->1748|3564->1774|3604->1776|3701->1837|3745->1859|3799->1881|4000->2046|4029->2053|4250->2246|4279->2247|4349->2289|4378->2290|4487->2371|4516->2372|4559->2387|4588->2388
                    LINES: 27->1|29->2|29->2|29->3|29->3|29->4|29->4|29->5|29->5|35->1|36->2|37->3|38->4|39->10|40->11|42->13|42->13|46->17|46->17|47->18|47->18|52->23|52->23|54->25|54->25|54->25|55->26|55->26|55->26|55->26|55->26|58->29|58->29|58->29|59->30|59->30|59->30|59->30|59->30|61->32|61->32|61->32|61->32|62->33|62->33|63->34|68->39|68->39|75->46|75->46|76->47|76->47|78->49|78->49|79->50|79->50
                    -- GENERATED --
                */
            
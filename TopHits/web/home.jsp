<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <% String login = (String) session.getAttribute("LoginFlag");
        if (login.equalsIgnoreCase("1")) {
    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <meta name="viewport" content="width=device-width,height=device-height, initial-scale=1, maximum-scale=1, user-scalable=no">

        <title>Lane Status</title>
        <link href="css/global.css" rel="stylesheet" type="text/css" />
        <link href="css/res.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript">
            if (/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
                var link = document.createElement('link');
                link.type = 'text/css';
                link.rel = 'stylesheet';
                link.href = 'css/checkbox.css';
                document.getElementsByTagName('head')[0].appendChild(link);

            }
        </script>

        <link rel="stylesheet" href="css/topHits.css" type="text/css" />
        <script src="js/jQuery 1.10.2.js" type="text/javascript"></script>
        <script type="text/javascript" src="js/topHits.js"></script>
        <script type='text/javascript' src='js/jquery.js'></script>
        <script type="text/javascript">
            function logOut() {
                document.forms[0].action = "/TopHits/Logout";
                document.forms[0].submit();
                return true;
            }
            $(document).ready(function() {
                $("#Tbtn").click(function() {
                    $("#mainContentArea").slideToggle("slow");
                    $(this).toggleClass("tOpen");
                });
            });

            //To Auto ReFresh Lane Colors via Ajax Call
            $(document).ready(function() {
                if ($("#lanes") || $("#planes")) {

                    setInterval(function() {
                        getColor();
                        checkComplete();
                        document.getElementById('lanes').style.display = "";
                        document.getElementById('planes').style.display = "";
                        document.getElementById('loader').style.display = "none";

                    }, 10000);
                }
                else
                {
                    document.getElementById('loader').style.display = "";
                }
            });

        </script>
        <%
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0); // Proxies.

            if (session.getAttribute("employeeId") == null
                    || session.getAttribute("employeeId") == "") {
                response.sendRedirect("index.jsp");
            }
        %>

    </head>
    <body onload="populateNearCriteria();
            populatePrinters();
            disableLinkAll();" >
        <form id="LanesForm" action="Logout" method="POST" ></form>
        <header class="header1024"><!--header start-->
            <div class="headBg" id="mainContentArea" style="display:none;"><!--headBg start-->
                <div class="wrapper"><!--wrapper start-->
                    <div class="headLeft"><!--headLeft start-->
                        <div class="basic-modal">
                            <a href="#" onclick="pop('popDiv1');
                                    populatePrinters();
                                    populateNearCriteria();">
                                <img src="images/gearIcon.png" width="59" alt="Settings" title="Settings"  />
                            </a>

                            <a href="javascript://" onclick="logOut();">
                                <img src="images/gridIcon.png" width="59" alt="Logout" title="Logout" />
                            </a>
                            <a href="#">
                                <img src="images/refreshicon.png" width="59" alt="Refresh" title="Refresh" onclick="getColor();" />
                            </a>
                        </div>
                    </div><!--headLeft start-->

                    <div class="headMiddle"><!--headMiddle start-->
                        <div class="headMiddleContent"><!--headMiddleContent start-->
                            <div class="headMiddleContentOne" >
                                <div class="greenMainLegend">
                                    <div class="greenLegend"><span class="colorBoxTextLegend">DEFAULT</span></div>
                                </div>
                            </div>
                            <div class="headMiddleContentOne">
                                <div class="redMainLegend">
                                    <div class="redLegend"><span class="colorBoxTextLegend">FULL</span></div>
                                </div>
                            </div>

                            <div class="headMiddleContentOne">
                                <div class="yellowMainLegend">
                                    <div class="yellowLegend"><span class="colorBoxTextLegend">NEAR</span></div>
                                </div>
                            </div>

                            <div class="headMiddleContentOne">
                                <div class="blueMainLegend">
                                    <div class="blueLegend"><span class="colorBoxTextLegend">COMPLETE</span></div>
                                </div>
                            </div>
                        </div><!--headMiddleContent end-->

                    </div><!--headMiddle end-->


                    <div class="headRight"><!--headRight start-->
                        <div class="headRightTop"><!--headRightTop start-->
                            <span>Lane Range:</span>
                            <input type="number" id="from" name="from" placeholder="" value="1" min="1" max="152" maxlength="3" required="required" />
                            <span>To</span>
                            <input type="number" id="to" name="to" placeholder="" value="152" min="1" max="152" maxlength="3" required="required" />
                            <input type="button" value="Go" onclick="displayRange();"/>
                        </div><!--headRightTop end-->
                        <div class="headRightBottom"><!--headRightBottom start-->

                            <div class="headRightBottomOdd"><!--headRightBottomOdd start--><a href="#" id="oddpress1" onclick="disableLinkOdd();
                                    showLinkEven();
                                    showLinkAll();
                                    showOdd();">Odd Lanes</a></div><!--headRightBottomOdd end-->
                            <div class="headRightBottomEven"><!--headRightBottomEven start--><a href="#" id="evenpress1" onclick="disableLinkEven();
                                    showLinkOdd();
                                    showLinkAll();
                                    showEven();">Even Lanes</a></div><!--headRightBottomEven end-->
                            <div class="headRightBottomAll"><!--headRightBottomAll start--><a href="#" id="allpress1" onclick="disableLinkAll();
                                    showLinkOdd();
                                    showLinkEven();
                                    showAll();">All Lanes</a></div><!--headRightBottomAll end-->

                        </div><!--headRightBottom end-->

                    </div><!--headRight end-->

                </div><!--wrapper end-->

            </div><!--headBg end-->

        </header><!--header end-->

        <header class="header600">
            <div class="headBgTwo"><!--headBgTwo start-->
                <div class="headLeftTwo"><!--headLeft start-->
                    <div class="basic-modal">
                        <a href="#" onclick="pop('popDiv1');
                                populatePrinters();
                                populateNearCriteria();">
                            <img src="images/gearIcon.png" width="59" alt="Settings" title="Settings"  />
                        </a>

                        <a href="javascript://" onclick="logOut();">
                            <img src="images/gridIcon.png" width="59" alt="Logout" title="Logout" />
                        </a>
                        <a href="#">
                            <img src="images/refreshicon.png" width="59" alt="Refresh" title="Refresh" style="padding-top:1px; float: right;" onclick="getColor();" />
                        </a>
                    </div>
                </div><!--headLeft start-->

                <div class="headMiddleTwo"><!--headMiddle start-->
                    <div class="headMiddleContent"><!--headMiddleContent start-->

                        <div class="headMiddleContentOne">
                            <div class="greenMain">
                                <div class="green"><span class="colorBoxTextLegend">Default</span></div>
                            </div>
                        </div>

                        <div class="headMiddleContentOne">
                            <div class="redMain">
                                <div class="red"><span class="colorBoxTextLegend">FULL</span></div>
                            </div>
                        </div>

                        <div class="headMiddleContentOne">
                            <div class="yellowMain">
                                <div class="yellow"><span class="colorBoxTextLegend">NEAR</span></div>
                            </div>
                        </div>

                        <div class="headMiddleContentOne">
                            <div class="blueMain">
                                <div class="blue"><span class="colorBoxTextLegend">COMPLETE</span></div>
                            </div>
                        </div>


                    </div><!--headMiddleContent end-->
                </div><!--headMiddle end-->

                <div class="headRight"><!--headRight start-->
                    <div class="headRightTop"><!--headRightTop start-->
                        <!-- <span>Lane Range:</span>-->

                        <input type="number" id="frompot" name="from" placeholder="" value="1" min="1" max="152" maxlength="3" required="required" />
                        <span>To</span>
                        <input type="number" id="topot" name="to" placeholder="" value="152" min="1" max="152" maxlength="3"required="required"/>
                        <input type="button" value="Go" onclick="populateFromTo();
                                displayRange();" style="margin: 0 1px 0 0;" />

                    </div><!--headRightTop end-->
                    <div class="headRightBottom"><!--headRightBottom start-->
                        <div class="headRightBottomOdd"><!--headRightBottomOdd start--><a href="#" id="oddpress2" onclick="disableLinkOdd();
                                showLinkEven();
                                showLinkAll();
                                showOdd();">Odd Lanes</a></div><!--headRightBottomOdd end-->
                        <div class="headRightBottomEven"><!--headRightBottomEven start--><a href="#" id="evenpress2" onclick="disableLinkEven();
                                showLinkOdd();
                                showLinkAll();
                                showEven();">Even Lanes</a></div><!--headRightBottomEven end-->
                        <div class="headRightBottomAll" ><!--headRightBottomAll start--><a href="#" id="allpress2" onclick="disableLinkAll();
                                showLinkOdd();
                                showLinkEven();
                                showAll();">All Lanes</a></div><!--headRightBottomAll end-->
                    </div><!--headRightBottom end-->
                </div><!--headRight end-->
            </div><!--headBgTwo end-->
        </header>


        <article><!--article start-->
            <section><!--section start-->
                <center><img id="loader" src="images/loading.gif" height="100" width="100" class="imgset"/></center>
                <div class="wrapper" style="display:none" id="lanes"><!--wrapper start-->
                    <div id="Tbtn" class="tBtnArea"><!--tBtnArea start-->
                        <img src="images/toggleBtn.png" width="100" alt="Toggle" title="Toggle" id="tog" /></div><!--tBtnArea end-->

                    <div  class="mainContentArea"><!--mainContentArea start-->
                        <div class="mainContentAreaOne"  ><!--mainContentAreaOne start-->
                            <%for (int i = 0; i < 152; i++) {
                            %>

                            <div class="noArea" id="lane<%=i%>"><!--noArea start-->
                                <div class="greenMain">
                                    <div id="btn_<%=i + 1%>" class="green basic-modal" ><a href="#" class="colorBoxTextNo" onclick="document.body.scrollTop = document.documentElement.scrollTop = 0;
                                            document.getElementById('carton').value =<%=i + 1%>;
                                            document.getElementById('lpn').value = '';
                                            popupselection(<%=i + 1%>)" ><%=i + 1%></a>
                                    </div>
                                </div>
                            </div>
                            <% }%>
                        </div>
                    </div><!--mainContentArea end-->
                </div>


                <div class="wrapperTwo" style="display:none;" id="planes"><!--wrapper start-->

                    <div  class="mainContentAreaTwo"><!--mainContentArea start-->
                        <div class="mainContentAreaOne"  ><!--mainContentAreaOne start-->
                            <%
                                for (int i = 0; i < 152; i++) {
                            %>
                            <div class="noArea" id="laneportrait<%=i%>"><!--noArea start-->
                                <div class="greenMain">
                                    <div id="pbtn_<%=i + 1%>" class="green basic-modal" ><a href="#" class="colorBoxTextNo" onclick="document.body.scrollTop = document.documentElement.scrollTop = 0;
                                            document.getElementById('carton').value =<%=i + 1%>;
                                            document.getElementById('lpn').value = '';
                                            popupselection(<%=i + 1%>)" ><%=i + 1%></a>
                                    </div>
                                </div>
                            </div>

                            <% }%>

                        </div><!--mainContentArea end-->
                    </div>
                </div><!--wrapper end-->

            </section><!--section end-->
        </article><!--article end-->

        <div id="popDiv" class="ontop">
            <form id="form1" onsubmit="return initiateclose();">
                <div class="basic-modal-content" id="popup">
                    <div class="basic-modal-content_Head">CLOSE CARTON CAPTURE
                        <div class="popCloseBtn"><a href="#" onClick="hide('popDiv')"><img src="images/x.png"/></a></div>
                    </div>
                    <div class="basic-modal-content_MainformArea" >
                        <div class="lArea"><label>CARTON#</label></div>
                        <input type="text" id="carton" name="carton" readonly="true" style="color: #ffffff"/>
                    </div>
                    <div class="basic-modal-content_MainformArea" style="height: 30px;">
                        <div class="lArea"><label>SCAN LPN<font color="red">*</font></label></div>
                        <input type="text" id="lpn" name="lpn" required="required" onkeyup="checkLpn();"   onblur="document.body.scrollTop = document.documentElement.scrollTop = 0;" />
                        <font size="1" ><div style="margin-left: 210px; margin-top: 5px;color: red;" id="errlpn"></div></font>
                    </div>
                    <div class="basic-modal-content_MainformArea" style="margin-top:-5px;" id="forcetr">
                        <div class="lArea"><label>FORCE AUDIT</label></div>
                        <input type="checkbox" height="10" width="10" onclick="show();" id="audit" name="audit" class="regular-checkbox" /><label for="audit"></label>
                    </div>
                    <div class="basic-modal-content_MainformArea"  id="commenttr" style="display:none;"  >
                        <div class="lArea"><label>COMMENT<font color="red">*</font></label></div>
                        <input type="text" height="10" width="10" id="auditcomment"  name="auditcomment" value="" onkeyup="checkComment();" />
                        <font size="1" ><div style="margin-left: 230px; margin-top: 2px;color: red;" id="errauditcomment">&nbsp;</div></font>
                    </div>

                    <div class="basic-modal-content_MainformAreaTwo" >
                        <input type="button" id="closecarton" value="OK" onclick="closeconfirm(document.getElementById('carton').value);"/>
                    </div>

                </div>
            </form>
        </div>
        <div id="popDiv1" class="ontop">
            <div class="seting">
                <form id="form2" onsubmit="return initiateconfirm();">
                    <div class="basic-modal-content" id="popupadmin">
                        <div class="basic-modal-content_Head">PREFERENCES
                            <div class="popCloseBtn"><a href="#" onClick="hide('popDiv1')"><img src="images/x.png"/></a></div>
                        </div>
                        <div class="basic-modal-content_MainformArea">
                            <div class="lArea"><label>NEAR FULL CRITERIA</label></div>
                            <div id="nc" ></div>
                            <!--                    <input type="text" id="nearcriteria" name="nearcriteria" value=""  />-->
                        </div>
                        <!--                    <div class="basic-modal-content_MainformArea">
                                                <div class="lArea"><label>Current Default Printer</label></div>
                                                <input type="text"  id="selectedprinter" value="" readonly="true" />
                                            </div>-->
                        <div class="basic-modal-content_MainformArea">
                            <div class="lArea"><label>DEFAULT PRINTER</label></div>
                            <select id="printerList" name="printer" >

                            </select>
                        </div>

                        <div class="basic-modal-content_MainformAreaTwo">
                            <input type="button" value="OK" id="confirmclose"  onclick="saveSettings();"/>
                        </div>
                    </div>

                </form>
            </div>
        </div>
        <div id="popDiv2" class="ontop">
            <div class="basic-modal-content" id="popupconfirm">
                <div class="basic-modal-content_Head">CONFIRMATION OF CLOSE
                    <div class="popCloseBtn"><a href="#" onClick="hide('popDiv2')"><img src="images/x.png"/></a></div>
                </div>
                <div class="basic-modal-content_MainformArea">
                    <div class="lArea"><label>CARTON#</label></div>
                    <input type="text" id="cartonfinal" name="cartonfinal" readonly="true" style="color: #ffffff"/>
                </div>
                <div class="basic-modal-content_MainformArea">
                    <div class="lArea"><label>SCAN LPN</label></div>
                    <input type="text" id="lpnfinal" name="lpnfinal" readonly="true" style="color: #ffffff"/>
                </div>
                <div class="basic-modal-content_MainformArea">
                    <div class="lArea"><label>CARTON QTY</label></div>
                    <input type="text" id="cartonqty" name="cartonqty" readonly="true" style="color: #ffffff"/>
                </div>
                <div class="basic-modal-content_MainformArea" id="labelaudit">
                    <div class="lArea"><label>LABEL FOR AUDIT</label></div>
                    <input type="text" id="auditfinal" name="auditfinal" readonly="true" style="color: #ff3300"/>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK" onclick="submitClosing();
                            getColor();"/>
                </div>
            </div>
        </div>

        <div id="popDiv3" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new"> A REQUEST TO CLOSE THIS LANE IS ALREADY PENDING <br>UNABLE TO CLOSE LANE AT THIS TIME
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv3');"/>
                </div>
            </div>
        </div>

        <div id="popDiv4" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new">SUCCESSFULLY INITIALIZED FOR CLOSING
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv4');"/>
                </div>
            </div>

        </div>

        <div id="popDiv5" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new">INITIALIZATION FAILED
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv5');"/>
                </div>
            </div>
        </div>
        <div id="popDiv6" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new">YOU HAVE ALREADY SELECTED <br>A LANE FOR CLOSING
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv6');"/>
                </div>
            </div>
        </div>

        <div id="popDiv7" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new" id="laneclose">LANE CLOSED SUCCESSFULLY
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="hidden" name="closedlane" id="closedlane"/>
                    <input type="button" value="OK" onclick="hide('popDiv7');
                            initializeLane(document.getElementById('closedlane').value);"/>
                </div>
            </div>


        </div>
        <div id="popDiv8" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new"> SUCCESSFULLY SAVED
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv8');"/>
                </div>
            </div>
        </div>
        <div id="popDiv9" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new"> NOT SUCCESSFULLY SAVED
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv9');"/>
                </div>
            </div>
        </div>
        <div id="popDiv10" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new"> NEAR CRITERIA SHOULD BE VALID
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv10');"/>
                </div>
            </div>
        </div>
        <div id="popDiv11" class="ontop">
            <div class="basic-modal-content" id="popupclosing">
                <div class="basic-modal-content_Head_new"> INVALID RANGE
                    <div class="popCloseBtn"></div>
                </div>

                <div class="basic-modal-content_MainformAreaTwo">
                    <input type="button" value="OK"  onclick="hide('popDiv11');"/>
                </div>
            </div>
        </div>
        <!--</form>-->
    </body>
    <%} else {
            response.sendRedirect("/Logout");
        }%>
</html>

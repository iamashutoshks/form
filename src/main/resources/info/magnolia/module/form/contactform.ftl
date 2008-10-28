[#assign cms=JspTaglibs["cms-taglib"]]
[#assign cmsu=JspTaglibs["cms-util-taglib"]]
[#include "header.inc.ftl"]

	<div id="nav-global">
		<h6>Navigation</h6>
        <ul>
        	<li id="nav-sec-home" class="on"><strong><span><em>You are here: </em>Home</span></strong></li>
            <li id="nav-sec-1"><a href="#"><span>Section one</span></a></li>
            <li id="nav-sec-2"><a href="#"><span>Section two</span></a></li>
            <li id="nav-sec-3"><a href="#"><span>Section three</span></a></li>
            <li id="nav-sec-4"><a href="#"><span>Section four</span></a></li>
         </ul>
	</div><!-- end nav -->

	<div id="section-header">
		<h6>The Section</h6>
		<p>Section Intro-Text goes here.</p>
	</div>


	<div id="wrapper-2">

		 <div id="nav">
		 	<div id="nav-box">
		 	<h6>Sub-Navigation</h6>
			<ul>
				<li><a href="#">Listitem</a></li>
				<li class="open">
                	<a href="#">Listitem two</a>
                    <ul>
                    	<li><a href="#">Listitem two.1</a></li>
                    	<li class="open">
                        	<a href="#">Listitem two.2</a>
							<ul>
								<li><a href="#">Listitem two.2.1</a></li>
								<li><a href="#">Listitem two.2.2</a></li>
								<li class="on"><strong><em>You are here: </em>Listitem two.2.3</strong></li>
							</ul>
						</li>
                    	<li><a href="#">Listitem two.3</a></li>
                	</ul>
                </li>
				<li><a href="#">Another Listitem</a></li>
				<li><a href="#">Listitemverylong</a></li>
				<li><a href="#">Listitem</a></li>
			</ul>
			</div><!-- end nav-box -->
		</div><!-- end nav -->

		<div id="wrapper-3">

			<div id="main">

                <div id="breadcrumb">
                    <h2>You are here:</h2>
                    <ol>
                        <li><a href="#">Homepage</a></li>
                        <li><a href="#">Lorem Ipsum</a></li>
                        <li><a href="#">Lorem Ipsum</a></li>
                    </ol>
                </div><!-- end Breadcrumb -->

				<h1>Form-Elements</h1>

            [@cms.editBar contentNodeName="form" paragraph="form" /]
            [@cms.includeTemplate contentNodeName="form"/]

            [@cms.editBar contentNodeName="form2" paragraph="form" /]
            [@cms.includeTemplate contentNodeName="form2"/]

</div><!-- end main -->

			<div id="extras">
            	<div id="extras-1">
                    <h2>Additional Information</h2>
                    <div class="box">
                        <h3><a href="#">Headline Sidebar</a></h3>
                        <a href="#"><img src="img/temp/dummy-2.png" alt="Dummy" /></a>
                        <p>Nam leo lectus, molestie non, commodo id, dignissim sit amet, tortor. Integer rhoncus commodo nisl. Praesent bibendum est non orci. <em class="more"><a href="#">read on <span> Headline Sidebar</span></a></em> </p>
                    </div><!-- end box -->
                    <div class="box">
                        <h3><a href="#">Another Headline Sidebar</a></h3>
                        <a href="#"><img src="img/temp/dummy-2.png" alt="Dummy" /></a>
                        <p>Nam leo lectus, molestie non, commodo id, dignissim sit amet, tortor. Integer rhoncus commodo nisl. Praesent bibendum est non orci. <em class="more"><a href="#">read on <span> Headline Sidebar</span></a></em></p>
                    </div><!-- end box -->
                    <div class="box">
                        <h3><a href="#">Another Headline Sidebar</a></h3>
                        <a href="#"><img src="img/temp/dummy-2.png" alt="Dummy" /></a>
                        <p>Nam leo lectus, molestie non, commodo id, dignissim sit amet, tortor. Integer rhoncus commodo nisl. Praesent bibendum est non orci. <em class="more"><a href="#">read on <span> Headline Sidebar</span></a></em></p>
                    </div><!-- end box -->
                </div><!-- #extras-1 -->
			</div><!-- end extras -->


		</div><!-- end wrapper-3 -->


		<div id="promos">

			<div class="promo" id="promo-1">
				<a href="#"><img src="img/temp/dummy-4.jpg" alt="Dummy" /></a>
				<div class="promo-text">
					<h3><a href="#">Headline Promo</a></h3>
					<p>Lorem ipsum dolor sit amet. <em class="more">read on <span> Headline Promo</span></em> </p>
				</div>
			</div>

			<div class="promo" id="promo-2">
				<a href="#"><img src="img/temp/dummy-4.jpg" alt="Dummy" /></a>
				<div class="promo-text">
					<h3><a href="#">Headline Promo</a></h3>
					<p>Lorem ipsum dolor sit amet. <em class="more">read on <span> Headline Promo</span></em> </p>
				</div>
			</div>

			<div class="promo" id="promo-3">
				<a href="#"><img src="img/temp/dummy-4.jpg" alt="Dummy" /></a>
				<div class="promo-text">
					<h3><a href="#">Headline Promo</a></h3>
					<p>Lorem ipsum dolor sit amet. <em class="more">read on <span> Headline Promo</span></em> </p>
				</div>
			</div>

			<div class="promo" id="promo-4">
				<a href="#"><img src="img/temp/dummy-4.jpg" alt="Dummy" /></a>
				<div class="promo-text">
					<h3><a href="#">Headline Promo</a></h3>
					<p>Lorem ipsum dolor sit amet. <em class="more">read on <span> Headline Promo</span></em> </p>
				</div>
			</div>

		</div><!-- end promos -->


	</div><!-- end wrapper-2 -->

	<div id="site-info">
		<p class="copyright">&#169; 2008 Magnolia Media Edition</p>
		<div>
			<h4>About us, Help and Feedback</h4>
			<ul>
				<li><a href="#">Contact us</a></li>
			    <li><a href="#">Sitemap</a></li>
				<li><a href="#">About us</a></li>
				<li><a href="#">Accessibility</a></li>
			</ul>
		</div>

		<div>
			<h4>Validation</h4>
			<ul>
				<li class="xhtml"><a href="#">XHTML</a></li>
			    <li class="css"><a href="#">CSS</a></li>
			</ul>
		</div>


	</div><!-- end site-info -->

</div><!-- end wrapper -->


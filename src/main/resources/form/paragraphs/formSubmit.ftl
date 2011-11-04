<style type="text/css">
.shopSearch .button, .form-wrapper .button, .navigation-previous input, .navigation-next input {
  background: #b90834 url(../img/bgs/submit.png) 0 0 repeat-x;
  width: auto;
  border: 1px solid #f19eb2;
  border-right: 1px solid #920728;
  border-bottom: 1px solid #920728;
  margin: 2px 4px;
  padding: 2px 10px;
  color: #fff;
  cursor: pointer;
  font-size: 120%;
  text-transform: uppercase;
  font-weight: bold;
  float:left;
}
 .navigation-previous input:hover,  .navigation-previous input:active,  .navigation-previous input:active,
 .navigation-next input:hover,  .navigation-next input:active,  .navigation-next input:active {
  background: #333;
  color: #fff;
  border: 1px solid #555;
  border-right: 1px solid #4d4d4d;
  border-bottom: 1px solid #4d4d4d;
}

.navigation-button-wrapper {
    clear: both;
    display: inline;
}
.navigation-previous, .navigation-next {
    float:left;
}
.form-wrapper div {
    clear: none;
}
.form-wrapper div.navigation-button-wrapper {
  width: 100%;
}

</style>

[#assign cms=JspTaglibs["cms-taglib"]]

[#assign backButtonText=content.backButtonText!]

<div class="navigation-button-wrapper" >
[@cms.editBar /]
	[#if backButtonText?has_content]
		<div class="navigation-previous">
        <input id="back-button" type="button" onclick="history.go(-1);return false;" value="${backButtonText?html}" />
        </div>
    [/#if]
        <div class="navigation-next">
    		<input type="submit" value="${content.buttonText!"Submit"?html}" />
    	</div>
    
    [#if mgnl.editMode]
    	<br />
	    <div class="condition" style="clear:both;">
	    	[@cms.contentNodeIterator contentNodeCollectionName="conditionList"]
                [@cms.includeTemplate /]
            [/@cms.contentNodeIterator]        
            <div style="width:103px">[@cms.newBar contentNodeCollectionName="conditionList" newLabel="${i18n['condition.newLabel']}" paragraph="formCondition" /]</div>
	    </div>
    [/#if]
</div>


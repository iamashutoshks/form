[#assign cms=JspTaglibs["cms-taglib"]]

[#assign backButtonText=content.backButtonText!]

<div class="button-wrapper" >
[@cms.editBar /]
	[#if backButtonText?has_content]
    		<input type="submit" value="${content.buttonText!"Submit"?html}" />
    		<input id="back-button" type="button" onclick="history.go(-1);return false;" value="${backButtonText?html}" />
    [#else]
    	<input type="submit" value="${content.buttonText!"Submit"?html}" />
    [/#if]
    
</div>


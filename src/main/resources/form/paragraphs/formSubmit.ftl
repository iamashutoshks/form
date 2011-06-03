[#assign cms=JspTaglibs["cms-taglib"]]

[#assign backButtonText=content.backButtonText!]

<div class="button-wrapper" >
[@cms.editBar /]
	[#if backButtonText?has_content]
        <input id="back-button" type="button" onclick="history.go(-1);return false;" value="${backButtonText?html}" />
    [/#if]
    <input type="submit" value="${content.buttonText!"Submit"?html}" />
</div>


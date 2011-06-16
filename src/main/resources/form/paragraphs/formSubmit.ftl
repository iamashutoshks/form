[#assign cms=JspTaglibs["cms-taglib"]]

[#assign backButtonText=content.backButtonText!]

<div class="button-wrapper" >
[@cms.editBar /]
	[#if backButtonText?has_content]
        <input id="back-button" type="button" onclick="history.go(-1);return false;" value="${backButtonText?html}" />
    [/#if]
    <input type="submit" value="${content.buttonText!"Submit"?html}" />
    [#if mgnl.editMode]
    	<br />
	    <div class="criteria">
	    	[@cms.contentNodeIterator contentNodeCollectionName="criteriaList"]
                [@cms.includeTemplate /]
            [/@cms.contentNodeIterator]        
            <div style="width:103px">[@cms.newBar contentNodeCollectionName="criteriaList" newLabel="${i18n['criteria.newLabel']}" paragraph="formCriteria" /]</div>
	    </div>
    [/#if]
</div>


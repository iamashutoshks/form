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
	    <div class="criteria" style="clear:both;">
	    	[@cms.contentNodeIterator contentNodeCollectionName="criteriaList"]
                [@cms.includeTemplate /]
            [/@cms.contentNodeIterator]        
            <div style="width:103px">[@cms.newBar contentNodeCollectionName="criteriaList" newLabel="${i18n['criteria.newLabel']}" paragraph="formCriteria" /]</div>
	    </div>
    [/#if]
</div>


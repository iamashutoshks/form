[#assign cms=JspTaglibs["cms-taglib"]]

<div id="${content.controlName}" >
[@cms.adminOnly]
    [@cms.editBar /]
[/@cms.adminOnly]
		[#if content.edits?exists]
		[@cms.contentNodeIterator contentNodeCollectionName="edits"]
           [@cms.includeTemplate/]
        [/@cms.contentNodeIterator]
        [/#if]
<div>[@cms.newBar contentNodeCollectionName="edits" paragraph="formGroupEdit" /]</div>
</div>

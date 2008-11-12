[#assign cms=JspTaglibs["cms-taglib"]]

<div id="${content.controlName}" >
[@cms.editBar /]
    [#if content.edits?exists]
        [@cms.contentNodeIterator contentNodeCollectionName="edits"]
            [@cms.includeTemplate/]
        [/@cms.contentNodeIterator]
    [/#if]
    [#if mgnl.editMode]
        <div>
            [@cms.newBar contentNodeCollectionName="edits" paragraph="formGroupEditItem" /]
        </div>
    [/#if]
</div>

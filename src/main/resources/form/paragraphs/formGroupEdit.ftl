[#assign cms=JspTaglibs["cms-taglib"]]

<div id="${content.controlName}" >
[@cms.editBar /]
    [#if content.edits?exists]
        [#if mgnl.editMode]
            ${i18n['form.note.field']}
        [/#if]
        [@cms.contentNodeIterator contentNodeCollectionName="edits"]
            [@cms.includeTemplate/]
        [/@cms.contentNodeIterator]
    [/#if]
    [#if mgnl.editMode]
        <div style="float:right;height:20px;width:100px">
            [@cms.newBar contentNodeCollectionName="edits" paragraph="formGroupEditItem" /]
        </div>
    [/#if]
</div>

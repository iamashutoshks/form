

[#assign divID = cmsfn.createHtmlAttribute("id", content.controlName!)]

<div ${divID!} >
    [@cms.edit /]

    [#if content.edits?exists]
        [#if cmsfn.editMode]
            <p>${i18n['form.note.field']}</p>
        [/#if]
    [/#if]

    [@cms.area name="edits" /]
</div>

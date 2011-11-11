
[#if content.controlName?has_content]
    <div id="${content.controlName}" >
[#else]
    <div>
[/#if]
[@cms.edit /]
    [#if content.edits?exists]
        [#if mgnl.editMode]
            ${i18n['form.note.field']}
        [/#if]

        [@cms.area name="edits" /]

    [/#if]
</div>

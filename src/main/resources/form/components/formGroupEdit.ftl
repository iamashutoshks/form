

[#assign divID = cmsfn.createHtmlAttribute("id", content.controlName!)]

<div ${divID!} >
    [@cms.edit /]

    [@cms.area name="edits" /]
</div>

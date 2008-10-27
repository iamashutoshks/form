[#assign cms=JspTaglibs["cms-taglib"]]

<div>
[@cms.adminOnly]
    <div >[@cms.editBar /]</div>
[/@cms.adminOnly]
    [#if content.title?has_content]
      <label for="${content.controlName}">
        ${content.title}
    [/#if]
        <input type="file" name="${content.controlName}" id="${content.controlName}" />
    [#if content.title?has_content]
      </label>
    [/#if]
  </div>

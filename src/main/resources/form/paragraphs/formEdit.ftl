[#assign cms=JspTaglibs["cms-taglib"]]

<div>
[@cms.adminOnly]
    <div >[@cms.editBar /]</div>
[/@cms.adminOnly]
    [#if content.title?has_content]
      <label for="${content.controlName}">
        ${content.title}
      [#if content.mandatory]
         <dfn title="required">*</dfn>
      [/#if]
    [/#if]
    [#if content.rows == 1]

        <input type="text" name="${content.controlName}" id="${content.controlName}" value="${params[content.controlName]!}"/>
    [#else]
        <textarea id="${content.controlName}" name="${content.controlName}" rows="${content.rows}"></textarea>
    [/#if]
    [#if content.title?has_content]
    	</label>
    [/#if]
    [#if content.description?has_content]
    	<span>${content.description}</span>
    [/#if]
</div>
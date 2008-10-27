[#assign cms=JspTaglibs["cms-taglib"]]
[#assign link=paragraphDef.resolveLink(content)]
[#assign name=paragraphDef.resolveName(content)]

<div>
[@cms.adminOnly]
    <div >[@cms.editBar /]</div>
[/@cms.adminOnly]
[#if content.lineAbove]<hr />[/#if]
     <a href="${link}" id="${content.controlName}">
      [#if content.text?has_content]
        ${content.text}
      [#else]
        [#if content.linkType == "internal"]
          ${name}
        [#else]
          ${link}
        [/#if]
      [/#if]
      </a>
[#list 1..content.spacer as i]
<br />
[/#list]
[@cms.adminOnly]
     <div style="clear:both;"> </div>
[/@cms.adminOnly]
</div>

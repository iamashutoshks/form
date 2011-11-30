

[@cms.edit /]

<div class="condition" style="clear:both;">
    [#list components as component ]
        [@cms.render content=component /]
    [/#list]
</div><!-- end condition -->
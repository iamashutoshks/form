

[#if cmsfn.editMode]
    <div class="condition-list">

        <div style="clear: both" >
            [@cms.edit/]
        </div>

        [#list components as component ]
            [@cms.render content=component /]
        [/#list]

    </div><!-- end condition-list -->
[/#if]
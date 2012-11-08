

[#if cmsfn.editMode]
    <div class="condition-list">
        <br/>

        [#list components as component ]
            [@cms.component content=component /]
        [/#list]

    </div><!-- end condition-list -->
[/#if]

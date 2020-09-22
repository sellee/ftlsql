MERGE INTO ${table_name}  T
USING ( SELECT
	  #${r"{"}obj_id${r"}"}  obj_id
	<#list col_list! as x>
	  ,#${r"{"}${x}${r"}"}  ${x}
	</#list>
	<#list col_list_cmm! as x>
	  ,#${r"{"}${x}${r"}"}  ${x}
	</#list>
	,#${r"{"}use_stat_cd${r"}"} use_stat_cd
) S  ON ( T.OBJ_ID  = S.OBJ_ID )
WHEN MATCHED THEN
    UPDATE SET
      <#list col_list! as x>
	   t.${x} = s.${x} <#if x_has_next> ,</#if>
	</#list>
	<#list col_list_cmm! as x>
	  , t.${x} = s.${x}
	</#list>
	, t.mdfy_dt = getdate(), t.fnl_evnt_dt = getdate(),  t.use_stat_cd = s.use_stat_cd
WHEN NOT MATCHED THEN
    INSERT (
	<#list col_list! as x>${x} <#if x_has_next> ,</#if> </#list>
	<#list col_list_cmm! as x>, ${x}</#list>
	,crt_dt,fnl_evnt_dt,use_stat_cd
    )
	VALUES (
	<#list col_list! as x>s.${x} <#if x_has_next> ,</#if> </#list>
	<#list col_list_cmm! as x>, s.${x}</#list>
	,getdate(),getdate(),s.use_stat_cd
    )
;
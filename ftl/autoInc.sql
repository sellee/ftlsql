[#-- 사용법: [@_pa_and 'a.' 'statCd' /]    camel 로 넘길것
결과:  and a.STAT_CD = #{statCd}
--]
[#macro _pa_and tb_alias camel_col_nm ]
[@_pa_where tb_alias camel_col_nm '=' 0 /]
[/#macro]

[#-- 사용법: [@_pa_like1 'a.' 'statCd' /]    camel 로 넘길것
결과:  and a.STAT_CD = #{statCd} + '%'
--]
[#macro _pa_like1 tb_alias camel_col_nm ]
[@_pa_where tb_alias camel_col_nm 'like' 1/]
[/#macro]

[#-- 사용법: [@_pa_like2 'a.' 'statCd' /]    camel 로 넘길것
결과:  and a.STAT_CD = #{statCd} + '%'
--]
[#macro _pa_like2 tb_alias camel_col_nm ]
[@_pa_where tb_alias camel_col_nm 'like' 2/]
[/#macro]


[#macro _pa_where tb_alias camel_col_nm opr percCnt]
[#if .data_model[camel_col_nm]?? ]
and [=tb_alias][= camel_col_nm?replace("[A-Z]", "_$0", 'r')?upper_case] [=opr] [#if percCnt > 1 ] '%' + [/#if] #{[= camel_col_nm]} [#if percCnt > 0 ] + '%' [/#if]
[/#if]
[/#macro]


[#--사용법:
  [@_pa_in ['a','b'] /] 결과:  '1','2','3'
  [@_pa_in lst0! /] 결과:  lst0 에 있는대로 '1','2','3'  !를 꼭 붙일것

  [@_pa_in_col lst_map! 'cd' /]
--]
[#macro _pa_in lst_in  ]
[#if lst_in!?is_enumerable]
[#list lst_in! as x]'[= x!?replace("'","''")]'[#if x_has_next], [/#if][/#list]
[#else]
'[= lst_in!?replace("'","''")]'
[/#if]
[/#macro]

[#macro _pa_in_col lst_in col]
[#if lst_in!?is_enumerable]
[#list lst_in! as x]'[=x[col]!?replace("'","''")]'[#if x_has_next], [/#if][/#list]
[/#if]
[/#macro]
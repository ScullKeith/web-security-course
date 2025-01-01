<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('phone_number'); section>
    <#if section = "header">
     <link rel="stylesheet" type="text/css" href="<link rel="stylesheet" type="text/css" href="/theme-resources/css/basic-data-registration-form.css">

    <#elseif section = "form">
				
			<form id="${properties.kcFormClass!}"  action="${url.loginAction}" method="post">
                
                <div>
                    <h2 id="${properties.kcFormHeader!}">${msg("joinCompanyText")}</h2> 
                </div>

                <div>

					<div>
                     <label class="${properties.kcInputLabel!}">First name</label>

						<input type="tel" id="full_name" name="full_name" class="${properties.kcCustomInput!}"
									 placeholder="Full name" required aria-invalid="<#if messagesPerField.existsError('full_name')>true</#if>"/>

              <#if messagesPerField.existsError('full_name')>
    
								<span id="input-error-full-name" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('full_name'))?no_esc}
								</span>
              </#if>
					</div>
	
                    <div>

                     <label class="${properties.kcInputLabel!}">Username</label> 

						<input type="tel" id="username" name="username" class="${properties.kcCustomInput!}"
									 placeholder="Username" required aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
              <#if messagesPerField.existsError('username')>

								<span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('username'))?no_esc}
								</span>
              </#if>
					</div> 

	<div>
                     <label class="${properties.kcInputLabel!}">Display name</label>

						<input type="tel" id="display_name" name="display_name" class="${properties.kcCustomInput!}"
									 placeholder="Display name" required aria-invalid="<#if messagesPerField.existsError('display_name')>true</#if>"/>

              <#if messagesPerField.existsError('display_name')>
    
								<span id="input-error-display-name" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('display_name'))?no_esc}
								</span>
              </#if>
					</div>

				</div>
                


					<div class="submit-button-wrapper">
                        <button class="submit-button" type="submit">
							<span class="button-text-continue"> Continue </span> 
        					<span class="button-text-arrow">></span> 
						</button>
					</div> 
					
						<div class="form-number-dots-wrapper">

					<div class="form-number-dot-selected"></div>
					<div class="form-number-dot"></div>
					<div class="form-number-dot"></div> 
					
				</div>
				</div>

				
			</form>
    </#if>
</@layout.registrationLayout> 
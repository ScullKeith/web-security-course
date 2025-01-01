<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
       <div></div> 
    <#elseif section = "form">

<form action="${url.loginAction}" method="post">

       <span class="${properties.kcCustomWarning!}">You need to verify your email</span>
       
                  <div>
				  	<h2>Check your email</h2>
                </div>
                 
                  <div>
                    <p>If the username or email you entered is associated with an account, we have sent an email for resetting your password. </p>
                    <p>Please click the link provided within the email in order to proceed further</p> 

                    <p>
                        <span style="background-color: yellow; color: black !important;"> 
                            Please check your spam folder
                        </span>
                        <span>
                            if you do not find the email within your main inbox. 
                        </span> 
                    </p> 
                </div>
                

                

    
				
                
			</form>
















      
    </#if>
</@layout.registrationLayout>
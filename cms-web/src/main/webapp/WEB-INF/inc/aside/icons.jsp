<div class="logonConnect">
	<em>其他帐号登录：</em>
	<p>
          <a href="javascript:void(0);" title="用MSN号登录" onclick="MSNLogin();" CATag="connect_msn"><i class="i iLogon i91"></i></a><a href="javascript:void(0);" openWindowURL='/regist/thirdlogin?thirdParty=qq' CATag="connect_qq" openWindowWidth="450" openWindowHeight="350" title="用QQ号登录"><i class="i iLogon i90"></i></a><a href="javascript:void(0);"  CATag="connect_renren" title="用人人网帐号登录" openWindowURL="/regist/thirdlogin?thirdParty=renren"  openWindowWidth="700" openWindowHeight="330"><i class="i iLogon i92"></i></a><a href="javascript:void(0);" title="用淘宝网帐号登录"  CATag="connect_taobao"  openWindowWidth="415" openWindowURL="/regist/thirdlogin?thirdParty=taobao"   openWindowHeight="530"><i class="i iLogon i93"></i></a><a href="javascript:void(0);" title="用百度帐号登录"  CATag="connect_baidu"  openWindowWidth="560" openWindowURL="/regist/thirdlogin?thirdParty=baidu"   openWindowHeight="300"><i class="i iLogon i94"></i></a><a href="javascript:void(0);" title="用飞信帐号登录"  CATag="connect_feixin"  openWindowWidth="560" openWindowURL="/regist/thirdlogin?thirdParty=feixin"   openWindowHeight="300"><i class="i iLogon i95"></i></a>
    </p>
	<a href="javascript:void(0);" class="btn btnLeft noDis" style="display:none;">展开</a>
</div>
<p class="robn">请遵守<a target="_blank" href="http://it.sohu.com/20111217/n330283040.shtml">《北京市微博客发展管理若干规定》</a><br>使用微博客服务</p>
<script type="text/javascript">
	function MSNLogin(){
		var _loginUrl="https://consent.live.com/Connect.aspx";
		var _urlParams="wrap_callback=http://"+ document.location.host +"/msn/callBack&wrap_client_id=000000004004F407&wrap_scope=WL_Activities.Update,WL_Contacts.View,WL_Profiles.View,Messenger.SignIn,IMControl.IMAllowAll,WL_Activities.View,WL_Photos.View&mkt=zh-cn&wrap_client_state=1";
	//	window.open(_loginUrl+"?"+_urlParams,"MSN SOHU","top=0, left=0,height=400,width=500");
		window.open(_loginUrl+"?"+_urlParams, "connect_window_open", "top=0, left=0,height=400,width=500");
	    window.CA.q("connect_msn");

	}

	var Icons=function(){
		this._init.apply(this,arguments);
	};
	Icons.prototype={
			_containerSelector:null,
			_labelSelector:null,
			//初始化方法
		    _init:function(containerSelector){
		        var self=this;
		        self._containerSelector=containerSelector;
		        self._labelSelector=containerSelector+" >em";
		        //self.isShowBtn();
		        self._bindEvent();

		    },
		    //隐藏Label
		    hideLabel:function($target,callBack){
		    	var self=this;
		    	 $target.animate({
		    			  width:'0px'
		    		  },1000,function(){
		    			  //$(event.currentTarget).find("span").css('display','none');
		    			  callBack();
		    		  });

		    },
		    //显示Label
		    showLabel:function($target,callBack){
		      var self=this;

			  $target.animate({
				  width:'65px'
			  },1000,function(){
				  callBack();
				  $target.css('display','inline');

			  });
		   },
		   isShowBtn:function(){
			   var self=this;
			   var $parentContener=jQuery(self._containerSelector);
			   var parentContenerWidth=$parentContener[0].offsetWidth;
			   var $container=jQuery(self._containerSelector+">p").css("width","auto").appendTo("body");
			   var containerWidth=$container[0].offsetWidth;

			   $container.css("width","80%").prependTo($parentContener);
			   jQuery(self._containerSelector+" em").prependTo($parentContener);
			   if(parentContenerWidth>=containerWidth){
				   jQuery(self._containerSelector+"> .btn").addClass("noDis");
				   $container.css("width","100%")
			   }

		   },
		   openWindow:function(url,openWindowWidth,openWindowHeight){
			   var self=this;
			   var thirdPart=window.open(url,"connect_window_open",'top=0, left=0,height='+openWindowHeight+',width='+openWindowWidth+',location=no,menubar=no,scrollbars=no,status=no,titlebar=no,toolbar=no,resizable=yes');
		   },
		   //绑定事件
		   _bindEvent:function(){
			   var self=this;

			   jQuery(self._containerSelector).bind("click",function(event){
				   var $target= jQuery(event.target);
					  var url=$target.closest("a")[0].getAttribute("openWindowURL");
	                  var openWindowWidth=$target.closest("a")[0].getAttribute("openWindowWidth");
	                  var openWindowHeight=$target.closest("a")[0].getAttribute("openWindowHeight");
	                  var CATag=$target.closest("a")[0].getAttribute("CATag");
					  if($target.hasClass("btnLeft")){
						  self.hideLabel(jQuery(self._labelSelector),function(){
							  $target.removeClass("btnLeft");
							  $target.addClass("btnRight");
						  });

					  }else if($target.hasClass("btnRight")){
						  self.showLabel(jQuery(self._labelSelector),function(){
							  $target.removeClass("btnRight");
							  $target.addClass("btnLeft");
						  });

					  }else if(url!=null&&url!=""){
	                      window.CA.q(CATag);
						  self.openWindow(url,openWindowWidth,openWindowHeight);
					  }
			   });

		   }
	};
	var icons=new Icons(".logonConnect");

</script>

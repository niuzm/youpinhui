package com.youpinhui.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.youpinhui.pojo.TbSeller;
import com.youpinhui.sellergoods.service.SellerService;
/**
 * 认证类
 * @author nzm
 *
 */
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private SellerService sellerService;
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("userdetailsservice");
		//构建角色列表ROLE_SELLER
		List<GrantedAuthority> grantAuths=new ArrayList();
		grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		
		TbSeller seller = sellerService.findOne(username);
		if(seller!=null) {
			if(seller.getStatus().equals("1")) {
				//返回用户对象， 会与输入的用户进行匹配
				return new User(username, seller.getPassword(), grantAuths);
			}
		}
		return null;
	}

}

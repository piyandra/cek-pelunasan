package org.cekpelunasan.service.users;

import lombok.RequiredArgsConstructor;
import org.cekpelunasan.entity.User;
import org.cekpelunasan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional
	public void insertNewUsers(Long chatId) {
		userRepository.save(User.builder()
			.chatId(chatId)
			.build());
	}


	@Transactional
	public Long countUsers() {
		return userRepository.count();
	}

	@Transactional
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	@Transactional
	public void deleteUser(Long chatId) {
		userRepository.deleteById(chatId);
	}

	@Transactional
	public Optional<User> findUserByChatId(Long chatId) {
		return userRepository.findById(chatId);
	}

	@Transactional
	public String findUserBranch(Long chatId) {
		Optional<User> byId = userRepository.findById(chatId);
		return byId.map(User::getBranch).orElse(null);
	}

	@Transactional
	public void saveUserBranch(Long chatId, String branch) {
		userRepository.findById(chatId).ifPresent(user -> {
			user.setBranch(branch);
			userRepository.save(user);
		});
	}
}

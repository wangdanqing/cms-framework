package net.pusuo.cms.web.service;

import com.google.common.collect.Maps;
import net.pusuo.cms.core.bean.IDSeq;
import net.pusuo.cms.web.dao.DaoFactory;
import net.pusuo.cms.web.dao.IDSeqDao;
import org.skife.jdbi.v2.DBI;

import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Id生成器
 *
 * @author 玄畅
 * @date 14-3-2 下午11:35
 */
public class IDSeqService {
	private static final int[] primer = {3, 5, 7, 11, 13, 17, 19, 23};
	private static final ConcurrentMap<String, AtomicLong> map = Maps.newConcurrentMap();
	private static final int fix_len = 1000;
	private static final String fmt_gateway = "%s_gateway";

	private final DBI dbi = DaoFactory.getEntityItemDBI();


	public long next(String group) {
		long ret;
		if (map.containsKey(group)) {
			String key_gateway = String.format(fmt_gateway, group);
			long id = map.get(group).get();
			int delta = gen(id);
			long current = map.get(group).addAndGet(delta);
			long gateway = map.get(key_gateway).get();
			ret = current;

			// update db
			if (current >= gateway) {
				IDSeqDao dao = dbi.onDemand(IDSeqDao.class);
				IDSeq seq = new IDSeq(group, current + fix_len);
				dao.update(seq);
				map.get(key_gateway).set(current + fix_len);
				System.out.println("===>> update" + (current + fix_len));
			}
		} else {
			//	load from db or init the id
			IDSeqDao dao = dbi.onDemand(IDSeqDao.class);
			IDSeq seq = dao.get(group);
			if (seq == null) {
				seq = new IDSeq(group, fix_len);
			}

			String key_gateway = String.format(fmt_gateway, group);
			map.put(group, new AtomicLong(seq.getId()));
			map.put(key_gateway, new AtomicLong(seq.getId() + fix_len));

			ret = seq.getId();
		}

		return ret;
	}

	/**
	 * 生成一个ID
	 *
	 * @param cursor 当前的游标
	 *
	 * @return id
	 */
	private int gen(long cursor) {
		int factor = primer[((int) (cursor % primer.length))];
		Random r = new Random();
		return r.nextInt(factor) + 1;
	}

	public static void main(String[] args) {
		IDSeqService seq = new IDSeqService();
		int i = 0;
		while (i < 10000) {
			i++;
			System.out.println(i + "||" + seq.next("abc"));
		}
	}

}

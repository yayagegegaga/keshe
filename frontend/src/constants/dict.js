// 业务状态码的中文展示字典；提交给后端时仍使用原有英文 code。
export const ANIMAL_TYPE = { CAT: '猫', DOG: '狗', OTHER: '其他' }
export const ANIMAL_GENDER = { MALE: '公', FEMALE: '母', UNKNOWN: '未知' }
export const ANIMAL_AGE = { BABY: '幼年', YOUNG: '青年', ADULT: '成年', OLD: '老年', UNKNOWN: '未知' }
export const ANIMAL_STATUS = {
  WAIT_RESCUE: '待救助', OBSERVING: '观察中', TREATING: '治疗中', ADOPTABLE: '可领养',
  APPLYING: '申请中', TRIAL: '试养中', ADOPTED: '已领养', NOT_ADOPTABLE: '不可领养', LOST: '已失踪'
}
export const ADOPTION_STATUS = {
  PENDING: '待审核', FIRST_APPROVED: '初审通过', INTERVIEWING: '面谈中', TRIAL: '试养中',
  SUCCESS: '领养成功', REJECTED: '已拒绝', CANCELED: '已取消', TRIAL_FAILED: '试养失败'
}
export const ADOPTION_OPERATION = {
  SUBMIT: '提交申请', FIRST_APPROVE: '初审通过', INTERVIEW: '面谈', TRIAL: '试养',
  SUCCESS: '领养成功', REJECT: '拒绝', CANCEL: '取消', TRIAL_FAILED: '试养失败'
}
export const TASK_STATUS = { WAIT_CLAIM: '待领取', CLAIMED: '已领取', FINISHED: '已完成', REVIEWED: '已审核', CANCELED: '已取消' }
export const TASK_TYPE = { FEEDING: '投喂', CLEANING: '清洁', MEDICAL: '医疗', FOLLOW_UP: '回访', TEMP_RESCUE: '临时救助' }
export const RESCUE_ORDER_STATUS = { WAIT_ASSIGN: '待分配', ASSIGNED: '已分配', PROCESSING: '处理中', WAIT_CONFIRM: '待确认', CLOSED: '已关闭', CANCELED: '已取消' }
export const RESCUE_ORDER_OPERATION = { CREATE: '创建工单', ASSIGN: '分配工单', START: '开始处理', FINISH: '提交完成', CLOSE: '关闭工单', CANCEL: '取消工单' }
export const NOTIFICATION_TYPE = { ADOPTION: '领养通知', TASK: '任务通知', RESCUE_ORDER: '工单通知', SYSTEM: '系统通知' }
export const ROLE = { SUPER_ADMIN: '系统管理员', ADMIN: '救助站管理员', USER: '普通用户', VOLUNTEER: '志愿者' }
export const RESCUE_CLUE_STATUS = { PENDING: '待审核', CONVERTED: '已转工单', INVALID: '无效线索', CANCELED: '已取消' }
export const EMERGENCY_LEVEL = { LOW: '低', MEDIUM: '中', HIGH: '高', URGENT: '紧急' }
export const dict = (map, code) => map[code] ?? code

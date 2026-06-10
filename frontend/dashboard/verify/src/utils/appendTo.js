/**
 * Created by gaopanfei on 2021/12/03.
 */
export default {
    inserted(el,binding){
        move(el,binding.value);
    },
    update(el,binding,vnode,oldValue){
        const value = binding.value;
        if(value !== oldValue){
            move(el,value);
        }
    }
};
function move(el,parentId) {
    const parentElement = document.getElementById(parentId);
    if(!parentElement)throw new Error(`id 为 ${parentId} 的节点不存在！`);
    const childLength = parentElement.childNodes.length;
    if(childLength !== 0 && parentElement.childNodes[childLength - 1] === el)return;
    parentElement.appendChild(el);
}
